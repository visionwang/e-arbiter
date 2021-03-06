import {Injectable} from '@angular/core';
import {Http} from '@angular/http';
import {CodeSubmitForm} from '../../../shared/interface/code-submit-form.interface';
import {environment} from 'environments/environment';
import {ModalService} from '../../../shared/service/modal.service';
import {MainPanelStream} from './main-panel.stream';
import {Task} from '../../tournament-management-panel/interface/task.interface';
import {Observable} from 'rxjs/Observable';
import {QuizSubmission} from '../tournament-details/tournament-details-quiz-upload.component';

@Injectable()
export class TaskService {

  constructor(private http: Http,
              private modalService: ModalService,
              private mainPanelStream: MainPanelStream) {
  }

  public submitCode(codeSubmitForm: CodeSubmitForm) {
    this.http.post(`${environment.server.api.url}/tournament/api/task/submit`, codeSubmitForm)
      .first()
      .map(res => res.json())
      .subscribe(
        data => this.modalService.showAlert(data.output, () => this.mainPanelStream.callLoadCurrentTournamentResults()),
        err => this.modalService.showAlert('Błąd wykonania'),
        () => this.mainPanelStream.callUpdateCurrentTournamentDetails()
      );
  }

  public submitQuiz(quizSubmission: QuizSubmission): Observable<any> {
    return this.http.post(`${environment.server.api.url}/tournament/api/task/submit/quiz`, quizSubmission)
      .map(res => res.json());
  }

  public getTask(tournamentId: string, taskId: string): Observable<Task> {
    return this.http.get(`${environment.server.api.url}/tournament/api/task/${tournamentId}/${taskId}`)
      .map(res => res.json());
  }
}
