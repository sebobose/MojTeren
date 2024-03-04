import { Component, inject } from '@angular/core';
import { FormBuilder, Validators } from '@angular/forms';
import { Router } from '@angular/router';
import { RegisterService } from './register.service';

@Component({
  selector: 'app-register',
  templateUrl: './register.component.html',
  styleUrl: './register.component.css',
})
export class RegisterComponent {
  private formBuilder = inject(FormBuilder);
  private registerService = inject(RegisterService);
  private router = inject(Router);

  userExists: boolean = false;
  activeButton: string = 'ATHLETE';
  contactString: string = 'Broj mobitela';
  registrationSuccess = false;
  registerForm = this.formBuilder.nonNullable.group(
    {
      email: ['', [Validators.required, Validators.email]],
      firstName: ['', [Validators.required]],
      lastName: ['', [Validators.required]],
      contactNumber: ['', [Validators.pattern('^[0-9]*$')]],
      password: ['', [Validators.required, Validators.minLength(8)]],
      role: [''],
    },
    {
      updateOn: 'blur',
    },
  );

  register() {
    this.registerForm.value.role = this.activeButton;
    if (
      (this.registerForm.valid &&
        this.registerForm.controls.contactNumber.value.length == 10) ||
      (this.registerForm.value.contactNumber === '' &&
        this.registerForm.controls.email.valid &&
        this.registerForm.controls.firstName.valid &&
        this.registerForm.controls.lastName.valid &&
        this.registerForm.controls.password.valid &&
        this.activeButton === 'ATHLETE')
    ) {
      this.registerService.register(this.registerForm.value).subscribe({
        next: (response) => {
          this.registrationSuccess = true;
          this.userExists = false;
          setTimeout(() => {
            this.router.navigate(['/login']);
          }, 3000);
        },
        error: (error) => {
          console.log('Error:', error);
          this.userExists = true;
        },
      });
    } else {
      console.log('Form is invalid');
    }
  }

  changeActive(role: string) {
    this.activeButton = role;
    if (role === 'ATHLETE') {
      this.contactString = 'Broj mobitela';
    } else if (role === 'FIELD_OWNER') {
      this.contactString = 'Broj mobitela*';
    }
  }
}
