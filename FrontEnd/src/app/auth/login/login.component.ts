import { Component, inject } from '@angular/core';
import { FormBuilder, Validators } from '@angular/forms';
import { Router } from '@angular/router';
import { LoginService } from './login.service';

@Component({
  selector: 'app-login',
  templateUrl: './login.component.html',
  styleUrl: './login.component.css',
})
export class LoginComponent {
  private formBuilder = inject(FormBuilder);
  private loginService = inject(LoginService);
  private router = inject(Router);

  loginForm = this.formBuilder.nonNullable.group(
    {
      email: ['', [Validators.required, Validators.email]],
      password: ['', [Validators.required, Validators.minLength(8)]],
    },
    {
      updateOn: 'change',
    },
  );
  userDoesntExist = false;

  login() {
    if (this.loginForm.valid) {
      this.loginService.login(this.loginForm.value).subscribe({
        next: (response) => {
          let data: any = response;
          localStorage.clear();
          localStorage.setItem('token', data.accessToken);
          localStorage.setItem('role', data.role);
          localStorage.setItem('email', data.email);
          switch (data.role) {
            case 'ADMIN':
              this.router.navigate(['/admin/requests']).then(() => {
                window.location.reload();
              });
              break;
            case 'FIELD_OWNER':
              this.router.navigate(['/home']).then(() => {
                window.location.reload();
              });
              break;
            case 'ATHLETE':
              this.router.navigate(['/home-athlete']).then(() => {
                window.location.reload();
              });
              break;
          }
        },
        error: (error) => {
          console.error('Error:', error);
          this.userDoesntExist = true;
        },
      });
    } else {
      console.error('Form is invalid');
    }
  }
}
