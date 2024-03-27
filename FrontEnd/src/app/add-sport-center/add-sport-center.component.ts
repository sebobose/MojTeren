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
  files: any = [];

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
    console.log(this.addForm);
    if (this.addForm.valid) {
      this.adminService.addSportCenter(this.addForm.value).subscribe({
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
    this.files.push(this.fileInput.files[0]);
    reader.onload = (_event) => {
      this.url.push(reader.result);
      const images = this.addForm.get('images') as FormArray;
      images.push(this.formBuilder.control(reader.result));
    };
  }

  onDeleteImg(id: number) {
    this.url.splice(id, 1);
    this.files.splice(id, 1);
    const images = this.addForm.get('images') as FormArray;
    images.removeAt(id);
  }
}
