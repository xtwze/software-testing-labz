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

@ExtendWith(MockitoExtension.class)
class TaskServiceTest {
    @Mock TaskStore store;
    private final Instant now = Instant.parse("2026-10-03T12:00:00Z");

    private TaskService service() {
        return new TaskService(store, Clock.fixed(now, ZoneOffset.UTC));
    }

    @Test
    void savesNormalizedTaskWithOwnerAndTime() {
        when(store.save(any(TaskItem.class))).thenAnswer(call -> call.getArgument(0));

        TaskItem actual = service().create("  Learn Java  ", "alice");

        ArgumentCaptor<TaskItem> captor = ArgumentCaptor.forClass(TaskItem.class);
        verify(store).save(captor.capture());
        TaskItem saved = captor.getValue();
        assertThat(saved.title()).isEqualTo("Learn Java");
        assertThat(saved.owner()).isEqualTo("alice");
        assertThat(saved.createdAt()).isEqualTo(now);
        assertThat(saved.id()).isNotNull();
        assertThat(actual).isEqualTo(saved);
    }

    @Test
    void invalidTitleDoesNotTouchRepository() {
        assertThatThrownBy(() -> service().create(" ", "alice"))
                .isInstanceOf(IllegalArgumentException.class);
        verifyNoInteractions(store);
    }

    @Test
    void duplicateIsNotSaved() {
        when(store.exists("alice", "Learn Java")).thenReturn(true);

        assertThatThrownBy(() -> service().create("Learn Java", "alice"))
                .isInstanceOf(IllegalStateException.class).hasMessage("duplicate title");
        verify(store, never()).save(any());
    }

    @Test
    void normalizedDuplicateDoesNotReachSave() {
        when(store.exists("alice", "Read Java")).thenReturn(true);

        assertThatThrownBy(() -> service().create("  Read Java  ", "alice"))
                .isInstanceOf(IllegalStateException.class).hasMessage("duplicate title");
        verify(store).exists("alice", "Read Java");
        verify(store, never()).save(any(TaskItem.class));
    }

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

    // Вариант Б.
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

    @Test
    void rejectsNormalizedDuplicateForBob() {
        when(store.exists("bob", "Review Android")).thenReturn(true);

        assertThatThrownBy(() -> service().create("  Review Android  ", "bob"))
                .isInstanceOf(IllegalStateException.class).hasMessage("duplicate title");
        verify(store).exists("bob", "Review Android");
        verify(store, never()).save(any(TaskItem.class));
    }

    @Test
    void tabsAndNewlinesDoNotTouchStore() {
        assertThatThrownBy(() -> service().create("\t\n", "bob"))
                .isInstanceOf(IllegalArgumentException.class);
        verifyNoInteractions(store);
    }

    @Test
    void savesTaskAtVariantBTime() {
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
