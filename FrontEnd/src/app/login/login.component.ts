import { Component, inject } from '@angular/core';
import { FormBuilder, ReactiveFormsModule, Validators } from '@angular/forms';
import { RouterLink } from '@angular/router';
import { LoginService } from './login.service';

@Component({
  selector: 'app-login',
  templateUrl: './login.component.html',
  styleUrl: './login.component.css',
})
export class LoginComponent {
  private formBuilder = inject(FormBuilder);
  private loginService = inject(LoginService);

  loginForm = this.formBuilder.nonNullable.group(
    {
      email: ['', [Validators.required, Validators.email]],
      password: ['', [Validators.required, Validators.minLength(8)]],
    },
    {
      updateOn: 'blur',
    },
  );
  userDoesntExist = false;

  login() {
    if (this.loginForm.valid) {
      this.loginService.login(this.loginForm.value).subscribe({
        next: (response) => {
          console.log('Response:', response);
          let data: any = response;
          localStorage.setItem('token', data.access_token);
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
