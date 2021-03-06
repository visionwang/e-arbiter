import {Injectable} from '@angular/core';
import {Http} from '@angular/http';
import {Observable} from 'rxjs/Observable';
import {TournamentPreview} from '../interface/tournament-preview.interface';
import {environment} from '../../../environments/environment';
import {Page} from '../interface/page.interface';

@Injectable()
export class TournamentPreviewService {

  constructor(private http: Http) {
  }

  public getUserActiveTournaments(pageNumber: number = 1, pageSize: number = 5, query: string = ''): Observable<Page<TournamentPreview>> {
    return this.http.get(`${environment.server.tournament.allTournamentsUrl}/active?page=${pageNumber - 1}&size=${pageSize}&query=${query}`)
      .map(res => res.json());
  }

  public getUserFinishedTournaments(pageNumber: number = 1, pageSize: number = 5, query: string = ''): Observable<Page<TournamentPreview>> {
    return this.http.get(`${environment.server.tournament.allTournamentsUrl}/finished?page=${pageNumber - 1}&size=${pageSize}&query=${query}`)
      .map(res => res.json());
  }

  public getNewestPublicTournaments(pageNumber: number = 1, pageSize: number = 5): Observable<Page<TournamentPreview>> {
    return this.http.get(`${environment.server.tournament.allTournamentsUrl}/newest?page=${pageNumber - 1}&size=${pageSize}`)
      .map(res => res.json());
  }

  public getMostPopularPublicTournaments(pageNumber: number = 1, pageSize: number = 5): Observable<Page<TournamentPreview>> {
    return this.http.get(`${environment.server.tournament.allTournamentsUrl}/popular?page=${pageNumber - 1}&size=${pageSize}`)
      .map(res => res.json());
  }

  public getAlmostEndedPublicTournaments(pageNumber: number = 1, pageSize: number = 5): Observable<Page<TournamentPreview>> {
    return this.http.get(`${environment.server.tournament.allTournamentsUrl}/ending?page=${pageNumber - 1}&size=${pageSize}`)
      .map(res => res.json());
  }

  public getDraftManageTournaments(pageNumber: number = 1, pageSize: number = 5, query: string = ''): Observable<Page<TournamentPreview>> {
    return this.http.get(`${environment.server.tournament.managementTournamentsUrl}/draft?page=${pageNumber - 1}&size=${pageSize}&query=${query}`)
      .map(res => res.json());
  }

  public getActiveManageTournaments(pageNumber: number = 1, pageSize: number = 5, query: string = ''): Observable<Page<TournamentPreview>> {
    return this.http.get(`${environment.server.tournament.managementTournamentsUrl}/active?page=${pageNumber - 1}&size=${pageSize}&query=${query}`)
      .map(res => res.json());
  }

  public getFinishedManageTournaments(pageNumber: number = 1, pageSize: number = 5, query: string = ''): Observable<Page<TournamentPreview>> {
    return this.http.get(`${environment.server.tournament.managementTournamentsUrl}/finished?page=${pageNumber - 1}&size=${pageSize}&query=${query}`)
      .map(res => res.json());
  }
}
