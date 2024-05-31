import { Component, inject, OnInit } from '@angular/core';
import { UserProfileService } from './user-profile.service';
import { FormBuilder, Validators } from '@angular/forms';
import { Router } from '@angular/router';

@Component({
  selector: 'app-user-profile',
  templateUrl: './user-profile.component.html',
  styleUrl: './user-profile.component.css',
})
export class UserProfileComponent implements OnInit {
  private userProfileService = inject(UserProfileService);
  private formBuilder = inject(FormBuilder);
  private router = inject(Router);

  public oldData: any;
  public roleMap: any = { ATHLETE: 'Sportaš', FIELD_OWNER: 'Vlasnik terena' };
  public editForm = this.formBuilder.nonNullable.group(
    {
      email: ['', [Validators.email]],
      firstName: ['', [Validators.required]],
      lastName: ['', [Validators.required]],
      contact: ['', [Validators.pattern('^[0-9]*$')]],
      role: ['', [Validators.required]],
    },
    {
      updateOn: 'blur',
    },
  );
  ngOnInit() {
    this.userProfileService.getUserProfile().subscribe({
      next: (data: any) => {
        this.oldData = data;
        this.editForm.controls.email.setValue(data.email);
        this.editForm.controls.email.disable();
        this.editForm.controls.firstName.setValue(data.firstName);
        this.editForm.controls.lastName.setValue(data.lastName);
        this.editForm.controls.role.setValue(this.roleMap[data.role]);
        this.editForm.controls.role.disable();
        if (data.contact) {
          this.editForm.controls.contact.setValue(data.contact);
        }
      },
      error: (error: any) => {
        console.log(error);
      },
    });
  }

  editProfile() {
    if (!this.dataChanged()) {
      return;
    }
    if (
      (this.editForm.valid &&
        this.editForm.controls.contact.value.length == 10) ||
      (this.editForm.controls.contact.value === '' &&
        this.editForm.controls.email.valid &&
        this.editForm.controls.firstName.valid &&
        this.editForm.controls.lastName.valid &&
        this.editForm.controls.role.value === 'ATHLETE')
    ) {
      console.log(this.editForm.value);
      this.userProfileService.editProfile(this.editForm.value).subscribe({
        next: () => {
          this.router.navigate(['/user-profile']).then(() => {
            window.location.reload();
          });
        },
        error: (error) => {
          console.log('Error editing profile:', error);
        },
      });
    } else {
      console.log('Form is invalid');
    }
  }

  private dataChanged() {
    return !(
      this.editForm.controls.firstName.value === this.oldData.firstName &&
      this.editForm.controls.lastName.value === this.oldData.lastName &&
      this.editForm.controls.contact.value === this.oldData.contact
    );
  }
}
