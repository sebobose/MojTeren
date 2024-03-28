import { Component, inject } from '@angular/core';
import { FormArray, FormBuilder, Validators } from '@angular/forms';
import { AdminService } from '../admin/admin.service';
import { Router } from '@angular/router';

@Component({
  selector: 'app-add-sport-center',
  templateUrl: './add-sport-center.component.html',
  styleUrl: './add-sport-center.component.css',
})
export class AddSportCenterComponent {
  private formBuilder = inject(FormBuilder);
  private adminService = inject(AdminService);
  private router = inject(Router);

  protected role = localStorage.getItem('role');
  url: any = [];
  fileInput: any;

  addForm = this.formBuilder.nonNullable.group(
    {
      email: ['', [Validators.email]],
      sportCenterName: ['', [Validators.required]],
      address: ['', [Validators.required]],
      images: this.formBuilder.array([]),
    },
    {
      updateOn: 'blur',
    },
  );

  addSportCenter() {
    if (this.addForm.valid) {
      let formData = new FormData();
      formData.append('email', this.addForm.controls.email.value);
      formData.append(
        'sportCenterName',
        this.addForm.controls.sportCenterName.value,
      );
      formData.append('address', this.addForm.controls.address.value);
      const images = this.addForm.get('images') as FormArray;
      for (let i = 0; i < this.addForm.controls.images.value.length; i++) {
        formData.append('images', images.at(i).value);
      }

      this.adminService.addSportCenter(formData).subscribe({
        next: () => {
          this.router.navigate(['/admin/sport-centers']).then(() => {
            window.location.reload();
          });
        },
      });
    }
  }

  handleFileInput(event: any): void {
    this.fileInput = event.target;
    let reader = new FileReader();
    reader.readAsDataURL(event.target.files[0]);
    const images = this.addForm.get('images') as FormArray;
    images.push(this.formBuilder.control(this.fileInput.files[0]));
    reader.onload = (_event) => {
      this.url.push(reader.result);
    };
  }

  onDeleteImg(id: number) {
    this.url.splice(id, 1);
    const images = this.addForm.get('images') as FormArray;
    images.removeAt(id);
  }
}
