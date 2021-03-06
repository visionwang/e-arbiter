import {ActivatedRouteSnapshot, CanActivate, RouterStateSnapshot} from '@angular/router';
import {Injectable} from '@angular/core';
import {AuthService} from '../service/auth.service';
import {Observable} from 'rxjs/Observable';
import {RouteService} from '../service/route.service';

@Injectable()
export class LoggedOutUserGuard implements CanActivate {

  constructor(private routeService: RouteService, private authService: AuthService) {
  }

  canActivate(route: ActivatedRouteSnapshot, state: RouterStateSnapshot): Observable<boolean>|boolean {

    if (!this.authService.isLoggedInUser() && !this.authService.getTokenFromLocalStorage()) {
      return true;
    }

    this.routeService.goToDashboard();
    return false;
  }
}
