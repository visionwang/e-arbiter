import {Component, Input} from '@angular/core';
import {TaskPreview} from '../../shared/interface/task-preview.interface';
import {TournamentStatus} from '../../shared/interface/tournament-status.enum';

@Component({
  selector: 'arb-tour-details-task-list',
  template: `
    <div class="tournament-details-card__tasks-panel">
      <div class="tournament-details-card__subtitle">
        <h4>Zadania</h4>
      </div>
      <div class="tournament-details-card__tasks-panel__tasks-list">
        <arb-tour-details-task-prev
          [taskPreview]="task"
          [status]="status"
          *ngFor="let task of taskPreviews;
          trackBy: trackByName"
        ></arb-tour-details-task-prev>
      </div>
    </div>
  `
})
export class TournamentDetailsTaskListComponent {
  @Input() taskPreviews: TaskPreview[];
  @Input() status: TournamentStatus;

  public trackByName(index: number, task: TaskPreview): string {
    return task.name;
  }
}
