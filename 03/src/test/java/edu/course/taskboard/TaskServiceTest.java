package edu.course.taskboard;

import java.time.Clock;
import java.time.Instant;
import java.time.ZoneOffset;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import static org.assertj.core.api.Assertions.*;
import static org.mockito.Mockito.*;

// Mockito создаст мок перед каждым тестом
@ExtendWith(MockitoExtension.class)
class TaskServiceTest {
    // здесь мок хранилища, настоящую базу не подключаем
    @Mock TaskStore store;
    private final Instant now = Instant.parse("2026-10-03T12:00:00Z");

    // мок уже готов, можно создать сервис с фиксированным временем
    private TaskService service() {
        return new TaskService(store, Clock.fixed(now, ZoneOffset.UTC));
    }

    // Проверяем, что ушло на сохранение
    @Test
    void savesNormalizedTaskWithOwnerAndTime() {
        // save вернёт ту же задачу, которую ему передали
        when(store.save(any(TaskItem.class))).thenAnswer(call -> call.getArgument(0));

        TaskItem actual = service().create("  Learn Java  ", "alice");

        // через каптор посмотрим аргумент save
        ArgumentCaptor<TaskItem> captor = ArgumentCaptor.forClass(TaskItem.class);
        // проверяем один вызов save и забираем переданную задачу
        verify(store).save(captor.capture());
        TaskItem saved = captor.getValue();
        // в isEqualTo пишем то, что ожидаем получить
        assertThat(saved.title()).isEqualTo("Learn Java");
        assertThat(saved.owner()).isEqualTo("alice");
        assertThat(saved.createdAt()).isEqualTo(now);
        assertThat(saved.id()).isNotNull();
        assertThat(actual).isEqualTo(saved);
    }

    // название из пробелов должно сразу вызвать ошибку
    @Test
    void invalidTitleDoesNotTouchRepository() {
        assertThatThrownBy(() -> service().create(" ", "alice"))
                .isInstanceOf(IllegalArgumentException.class);
        // тут не должно быть даже вызова exists
        verifyNoInteractions(store);
    }

    // Повторную задачу не сохраняем
    @Test
    void duplicateIsNotSaved() {
        when(store.exists("alice", "Learn Java")).thenReturn(true);

        assertThatThrownBy(() -> service().create("Learn Java", "alice"))
                .isInstanceOf(IllegalStateException.class).hasMessage("duplicate title");
        verify(store, never()).save(any());
    }

    // проверяем дубликат с пробелами по краям
    @Test
    void normalizedDuplicateDoesNotReachSave() {
        when(store.exists("alice", "Read Java")).thenReturn(true);

        assertThatThrownBy(() -> service().create("  Read Java  ", "alice"))
                .isInstanceOf(IllegalStateException.class).hasMessage("duplicate title");
        verify(store).exists("alice", "Read Java");
        verify(store, never()).save(any(TaskItem.class));
    }

    // передаём другое время, чтобы проверить clock
    @Test
    void creationUsesTheClockPassedToTheConstructor() {
        Instant expected = Instant.parse("2026-10-16T08:15:00Z");
        TaskService subject = new TaskService(store, Clock.fixed(expected, ZoneOffset.UTC));
        when(store.save(any(TaskItem.class))).thenAnswer(call -> call.getArgument(0));

        TaskItem actual = subject.create("Check clock", "alice");

        ArgumentCaptor<TaskItem> captor = ArgumentCaptor.forClass(TaskItem.class);
        verify(store).save(captor.capture());
        assertThat(captor.getValue().createdAt()).isEqualTo(expected);
        assertThat(actual.createdAt()).isEqualTo(expected);
    }

    // Вариант Б
    // 1. создаём задачу для bob
    @Test
    void savesReviewAndroidForBob() {
        when(store.save(any(TaskItem.class))).thenAnswer(call -> call.getArgument(0));

        TaskItem actual = service().create("  Review Android  ", "bob");

        ArgumentCaptor<TaskItem> captor = ArgumentCaptor.forClass(TaskItem.class);
        verify(store).save(captor.capture());
        TaskItem saved = captor.getValue();
        assertThat(saved.title()).isEqualTo("Review Android");
        assertThat(saved.owner()).isEqualTo("bob");
        assertThat(actual).isEqualTo(saved);
    }

    // 2. повторно отправляем то же название
    @Test
    void rejectsNormalizedDuplicateForBob() {
        // считаем, что у bob уже есть такая задача
        when(store.exists("bob", "Review Android")).thenReturn(true);

        // ждём исключение при попытке создать дубликат
        assertThatThrownBy(() -> service().create("  Review Android  ", "bob"))
                .isInstanceOf(IllegalStateException.class).hasMessage("duplicate title");
        // проверяем, что в поиск пришло название без пробелов
        verify(store).exists("bob", "Review Android");
        // save вызываться не должен
        verify(store, never()).save(any(TaskItem.class));
    }

    // 3. вместо названия передаём табуляцию и перенос строки
    @Test
    void tabsAndNewlinesDoNotTouchStore() {
        assertThatThrownBy(() -> service().create("\t\n", "bob"))
                .isInstanceOf(IllegalArgumentException.class);
        // тут не должно быть даже вызова exists
        verifyNoInteractions(store);
    }

    // 4. проверяем время из условия варианта
    @Test
    void savesTaskAtVariantBTime() {
        // фиксируем время, чтобы тест не зависел от даты запуска
        Instant expected = Instant.parse("2026-10-03T18:30:00Z");
        TaskService subject = new TaskService(store, Clock.fixed(expected, ZoneOffset.UTC));
        when(store.save(any(TaskItem.class))).thenAnswer(call -> call.getArgument(0));

        TaskItem actual = subject.create("Review Android", "bob");

        ArgumentCaptor<TaskItem> captor = ArgumentCaptor.forClass(TaskItem.class);
        verify(store).save(captor.capture());
        assertThat(captor.getValue().createdAt()).isEqualTo(expected);
        assertThat(actual.createdAt()).isEqualTo(expected);
    }
}
