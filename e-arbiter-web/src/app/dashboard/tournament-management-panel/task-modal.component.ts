import {Component, Input, ViewChild} from "@angular/core";
import TaskModel, {Task} from "app/dashboard/tournament-management-panel/interface/task.interface";
import {FormArray, FormBuilder} from "@angular/forms";
import {SemanticModalComponent} from "ng-semantic/ng-semantic";

@Component({
  selector: 'arb-task-modal',
  template: `
    <sm-modal title="Dodaj zadanie" #innerTaskModal>
      <modal-content *ngIf="task">
        <form #f="ngForm" class="ui form">
          <div class="two fields">
            <div class="field">
              <label>Nazwa</label>
              <input type="text" name="name" [(ngModel)]="task.name">
            </div>
            <div class="field">
              <label>Typ</label>
              <select name="type" [(ngModel)]="task.type">
                <option *ngFor="let type of taskTypes" [value]="type.value">{{ type.display }}</option>
              </select>
            </div>
          </div>
          <div class="field">
            <label>Opis</label>
            <textarea rows="4" name="description" [(ngModel)]="task.description"></textarea>
          </div>
          <div *ngIf="task.type === 'CodeTask'" class="two fields">
            <div class="field">
              <label>Jezyki programowania</label>
              <sm-select 
                placeholder="Wybierz..."
                class="fluid search multiple"
                (onChange)="task.languages = $event"
              >
                <option *ngFor="let language of languages" [value]="language">{{ language }}</option>
              </sm-select>
            </div>
            <div class="field">
              <label>Timeout (ms)</label>
              <input type="number" name="timeoutInMs" [(ngModel)]="task.timeoutInMs"/>
            </div>
          </div>
        </form>
      </modal-content>
      <modal-actions>
        <div class="ui buttons">
          <div class="ui cancel button">Odrzuć</div>
          <div class="ui ok button primary">Zapisz</div>
        </div>
      </modal-actions>
    </sm-modal>
  `
})
export class TaskModalComponent {

  @Input() tasks: FormArray;

  @ViewChild("innerTaskModal") innerTaskModal: SemanticModalComponent;
  task: Task;

  taskTypes = TaskModel.taskTypes;
  languages = TaskModel.languages;

  constructor(private fb: FormBuilder) {}

  public addTask() {
    this.task = TaskModel.createEmptyTask();

    this.innerTaskModal.show({
      closable: false,
      onApprove: () => this.addToFormArray(this.task)
    });
  }

  private addToFormArray(task: Task) {
    console.log(task);
    if (this.isValid(task)) {
      this.tasks.push(
        this.fb.group({
          type: [task.type],
          name: [task.name],
          description: [task.description],
          codeTaskTestSets: [task.codeTaskTestSets],
          questions: [task.questions],
          timeoutInMs: [task.timeoutInMs],
          languages: [task.languages]
        })
      );
    }
  }

  private isValid(task: Task): boolean {
    return true;
  }

}
