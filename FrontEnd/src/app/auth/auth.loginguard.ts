import { Injectable } from '@angular/core';
import {ActivatedRouteSnapshot, Router, RouterStateSnapshot} from '@angular/router';
import { AuthService } from './auth.service';
// import {HeaderService} from "../header/header.service";

@Injectable({
  providedIn: 'root'
})
export class AuthLoginGuard  {

  // constructor(private auth : AuthService, private router : Router, private headerService: HeaderService) {}

  canActivate() {
    // if(this.auth.isUserLoggedIn()) {
    //   alert("Vec ste prijavljeni, za ponovnu prijavu ili registraciju potrebna je odjava!")
    //   this.router.navigate(['']);
    //   const userRole = this.auth.getUserRole();
    //   if (userRole === "tragac") {
    //     this.headerService.changeActivePage("/explorer-tasks")
    //     this.router.navigate(['/explorer-tasks']);
    //     return false;
    //   } else if (userRole === "istrazivac") {
    //     this.headerService.changeActivePage("/researcher-profile")
    //     this.router.navigate(['/researcher-profile']);
    //     return false;
    //   } else if (userRole === "admin") {
    //     this.headerService.changeActivePage("/users-list")
    //     this.router.navigate(['/users-list']);
    //     return false;
    //   } else if (userRole === "voditeljPostaje") {
    //     this.headerService.changeActivePage("/station-leader-profile")
    //     this.router.navigate(['/station-leader-profile']);
    //     return false;
    //   }
    // }
    return true;
  }
}
