import { Routes } from '@angular/router';
import {AuthLoginGuard} from "./auth/auth.loginguard";
import {LoginComponent} from "./login/login.component";
import {RegisterComponent} from "./register/register.component";

export const routes: Routes = [
  { path: '', redirectTo: '/login', pathMatch: 'full' },
  { path: 'login', component: LoginComponent, canActivate:[AuthLoginGuard]},
  { path: 'register' , component: RegisterComponent, canActivate:[AuthLoginGuard]},
];
