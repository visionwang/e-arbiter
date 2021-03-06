import {FormArray, FormBuilder, FormControl, FormGroup, Validators} from '@angular/forms';
import {Component, OnInit, ViewChild} from '@angular/core';
import {Translations} from '../../shared/model/calendar.model';
import {Tournament} from './interface/tournament.interface';
import {TaskModalComponent} from './task-modal.component';
import {TournamentManagementService} from './tournament-management.service';
import {TournamentStatus} from '../../shared/interface/tournament-status.enum';
import {ModalService} from '../../shared/service/modal.service';
import {DateService} from '../../shared/service/date.service';
import {ActivatedRoute} from '@angular/router';
import {RouteService} from '../../shared/service/route.service';

declare var $: any;

@Component({
  selector: 'arb-tournament-form',
  template: `
    <div class="ui container center aligned scrollable-page-view">
      <form [formGroup]="myForm" class="ui form" (ngSubmit)="save(myForm.value)">
        <h2 class="ui dividing header">Tworzenie Turnieju - Szablon</h2>
        <div class="two fields">
          <div class="field" [ngClass]="{ 'error' : isControlInvalidAndTouched('name') }">
            <label>Nazwa</label>
            <input type="text" formControlName="name"/>
            <div
              *ngIf="isControlInvalidAndTouched('name')"
              class="ui basic red pointing prompt label"
            >Nazwa turnieju powinna być długości od 3 do 64 znaków.
            </div>
          </div>
          <div class="field" [ngClass]="{ 'error' : isControlInvalidAndTouched('endDate') }">
            <label>Do kiedy</label>
            <div class="ui calendar" id="calendar">
              <div class="ui input left icon">
                <i class="calendar icon"></i>
                <input type="text" formControlName="endDate">
              </div>
            </div>
            <div
              *ngIf="isControlInvalidAndTouched('endDate')"
              class="ui basic red pointing prompt label"
            >Data zakończenia turnieju jest wymagana i musi być późniejsza niż data obecna.
            </div>
          </div>
        </div>
        <div class="field">
          <label>Opis</label>
          <textarea rows="3" formControlName="description"></textarea>
        </div>
        <div class="pull-right inline fields">
          <div class="field">
            <div class="ui radio checkbox">
              <input type="radio" name="publicFlag" formControlName="publicFlag"
                     [checked]="myForm.controls['publicFlag'].value" [value]="true">
              <label>Publiczny</label>
            </div>
          </div>
          <div class="field">
            <div class="ui radio checkbox">
              <input type="radio" name="publicFlag" formControlName="publicFlag"
                     [checked]="!myForm.controls['publicFlag'].value" [value]="false">
              <label>Prywatny</label>
            </div>
          </div>
        </div>
        <div class="pull-right inline fields">
          <div class="ui checkbox">
            <input type="checkbox" formControlName="resultsVisibleForJoinedUsers"
                   [checked]="myForm.controls['resultsVisibleForJoinedUsers'].value">
            <label>Wyniki dostępne dla uczestników</label>
          </div>
        </div>
        <div *ngIf="myForm.controls['tasks']['controls'].length > 0" class="four fields">
          <div class="field">
            <label>Zadania</label>
            <div class="ui middle aligned divided list">
              <div *ngFor="let taskControl of myForm.controls['tasks']['controls']; let i = index"
                   class="item task-item">
                <div class="right floated content">
                  <i class="minus icon" (click)="myForm.controls['tasks'].removeAt(i)"></i>
                  <i class="edit icon" (click)="taskModal.editTask(taskControl.value)"></i>
                </div>
                <div class="content">
                  <div>{{ taskControl.value.name }}</div>
                </div>
              </div>
            </div>
          </div>
        </div>
        <div class="pull-right inline fields">
          <div class="field">
            <button class="ui large teal button" type="button" (click)="taskModal.addTask()">
              <i class="add circle icon"></i>
              Dodaj zadanie
            </button>
          </div>
        </div>
        <div class="pull-center inline fields" *ngIf="!myForm.controls['publicFlag'].value">
          <div class="field" [ngClass]="{ 'error' : isControlInvalidAndTouched('password') }">
            <label>Hasło</label>
            <input [type]="showPassword ? 'text' : 'password'" formControlName="password"/>
          </div>
          <div class="ui checkbox">
            <input type="checkbox" [checked]="showPassword" (change)="showPassword = !showPassword"/>
            <label>Pokaż</label>
          </div>
        </div>
        <div class="main-button-container">
          <button [disabled]="!myForm.valid" class="ui teal button huge" type="submit">
            {{ inEditMode ? 'Zapisz' : 'Utwórz' }}
          </button>
        </div>
      </form>

      <!-- MODAL -->
      <arb-task-modal #taskModal [tasks]="myForm.controls['tasks']"></arb-task-modal>
    </div>
  `
})
export class TournamentFormComponent implements OnInit {
  myForm: FormGroup;
  showPassword: boolean;
  inEditMode: boolean;

  @ViewChild('taskModal') taskModal: TaskModalComponent;

  constructor(private fb: FormBuilder,
              private tournamentManagementService: TournamentManagementService,
              private modalService: ModalService,
              private dateService: DateService,
              private route: ActivatedRoute,
              private routeService: RouteService) {
  }

  ngOnInit() {

    this.route.params.first().subscribe(
      params => {
        if (params['id']) {
          this.tournamentManagementService.getById(params['id']).first().subscribe(
            tournament => this.updateForm(tournament)
          )
        }
      }
    );

    $('#calendar').calendar({
      ampm: false,
      text: {
        days: Translations.DAYS,
        months: Translations.MONTHS,
        monthsShort: Translations.MONTHS_SHORT,
        today: Translations.TODAY,
        now: Translations.NOW
      },
      onChange: (date: Date) => {
        this.myForm.controls.endDate.setValue(this.dateService.parseDateToString(date));

        if (this.dateService.isPassedDateLaterThanNow(date)) {
          this.myForm.controls.endDate.setErrors(null);
        } else {
          this.myForm.controls.endDate.setErrors({'incorrect': true});
        }
      }
    });

    this.myForm = this.fb.group({
      name: ['', [Validators.required, Validators.minLength(3), Validators.maxLength(64)]],
      endDate: ['', Validators.required],
      description: [''],
      publicFlag: [true],
      resultsVisibleForJoinedUsers: [false],
      password: [''],
      status: [TournamentStatus.DRAFT],
      tasks: this.fb.array([], Validators.compose([Validators.required, Validators.minLength(1)]))
    });

    this.myForm.get('publicFlag').valueChanges
      .subscribe(publicFlag => this.onPublicFlagChange(publicFlag));
  }

  isControlInvalidAndTouched(controlName: string): boolean {
    return !this.myForm.controls[controlName].valid &&
      this.myForm.controls[controlName].touched;
  }

  private onPublicFlagChange(publicFlag: boolean): void {
    const passwordControl = this.myForm.get('password');

    if (!publicFlag) {
      passwordControl.setValidators([Validators.required]);
    } else {
      passwordControl.setValidators([]);
    }

    passwordControl.updateValueAndValidity();
  }

  private updateForm(tournament: Tournament): void {
    this.inEditMode = true;
    this.myForm.addControl('id', new FormControl(tournament.id));

    const controls = this.myForm.controls;
    controls['name'].setValue(tournament.name);
    controls['description'].setValue(tournament.description);
    controls['publicFlag'].setValue(tournament.publicFlag);
    controls['resultsVisibleForJoinedUsers'].setValue(tournament.resultsVisibleForJoinedUsers);
    tournament.tasks.forEach(task => {
      (<FormArray>this.myForm.controls['tasks']).push(this.fb.group({
        type: [task.type],
        name: [task.name],
        description: [task.description],
        codeTaskTestSets: [task.codeTaskTestSets],
        questions: [task.questions],
        timeoutInMs: [task.timeoutInMs],
        languages: [task.languages],
        maxAttempts: [task.maxAttempts]
      }));
    });
    $('#calendar').calendar('set date', new Date(tournament.endDate));
  }

  save(tournament: Tournament) {
    const ms = this.modalService;

    this.tournamentManagementService.saveTournament(tournament)
      .subscribe(
        data => ms.showAlert(
          `Pomyślnie ${this.inEditMode ? 'zaaktualizowano' : 'utworzono'} turniej`,
          () => this.routeService.goToTournamentManagement()
        ),
        err => ms.showAlert('Nie można dodać turnieju. Spróbuj jeszcze raz lub spytaj administratora o przyczyny')
      );
  }
}
