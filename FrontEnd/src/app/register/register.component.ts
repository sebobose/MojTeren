import { Component, inject, OnInit } from '@angular/core';
import { FormBuilder, ReactiveFormsModule, Validators } from '@angular/forms';
import { RouterLink } from '@angular/router';
import { NgClass } from '@angular/common';

@Component({
  selector: 'app-register',
  standalone: true,
  imports: [ReactiveFormsModule, RouterLink, NgClass],
  templateUrl: './register.component.html',
  styleUrl: './register.component.css',
})
export class RegisterComponent {
  private formBuilder = inject(FormBuilder);

  activeButton: string = 'athlete';
  contactString: string = 'Broj mobitela (opcionalno)';
  registerForm = this.formBuilder.nonNullable.group(
    {
      email: ['', [Validators.required, Validators.email]],
      firstName: ['', [Validators.required]],
      lastName: ['', [Validators.required]],
      contactNumber: ['', [Validators.pattern('^[0-9]*$')]],
      password: ['', [Validators.required, Validators.minLength(8)]],
    },
    {
      updateOn: 'blur',
    },
  );

  register() {}

  changeActive(role: string) {
    this.activeButton = role;
    if (role === 'athlete') {
      this.contactString = 'Broj mobitela (opcionalno)';
    } else if (role === 'fieldOwner') {
      this.contactString = 'Broj mobitela';
    }
  }
}
