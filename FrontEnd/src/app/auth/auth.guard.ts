// import { Injectable } from '@angular/core';
// import {ActivatedRouteSnapshot, Router, RouterStateSnapshot} from '@angular/router';
// import { AuthService } from './auth.service';
// import {HeaderService} from "../header/header.service";
//
// @Injectable({
//     providedIn: 'root'
// })
// export class AuthGuard  {
//
//     constructor(private auth : AuthService, private router : Router, private headerService: HeaderService) {}
//
//   canActivate(
//   next: ActivatedRouteSnapshot,
//   state: RouterStateSnapshot
// ) {
//       const requiredRole = next.data['requiredRole'];
//       if(!this.auth.isUserLoggedIn()) {
//           this.headerService.changeActivePage("/login");
//           this.router.navigate(["/login"]);
//           return false;
//       }
//
//       const userRole = this.auth.getUserRole();
//
//       if (requiredRole.includes(userRole)) {
//         return true;
//       } else {
//
//         if(userRole === "tragac") {
//           this.headerService.changeActivePage("/explorer-tasks");
//           this.router.navigate(['/explorer-tasks']);
//         } else if(userRole === "istrazivac") {
//           this.headerService.changeActivePage("/researcher-profile");
//           this.router.navigate(['/researcher-profile']);
//         } else if(userRole === "voditeljPostaje") {
//           this.headerService.changeActivePage("/station-leader-profile");
//           this.router.navigate(['/station-leader-profile']);
//         } else if(userRole === "admin") {
//           this.headerService.changeActivePage("/users-list");
//           this.router.navigate(['/users-list'])
//         }
//         return false;
//       }
//     }
// }
