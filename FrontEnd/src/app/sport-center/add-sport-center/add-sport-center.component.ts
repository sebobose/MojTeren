import { Component, inject, OnInit } from '@angular/core';
import { FormArray, FormBuilder, Validators } from '@angular/forms';
import { AdminService } from '../../admin/admin.service';
import { Router } from '@angular/router';
import { SportCenterService } from '../sport-center.service';

@Component({
  selector: 'app-add-sport-center',
  templateUrl: './add-sport-center.component.html',
  styleUrl: './add-sport-center.component.css',
})
export class AddSportCenterComponent implements OnInit {
  private formBuilder = inject(FormBuilder);
  private sportCenterService = inject(SportCenterService);
  private router = inject(Router);

  protected role = localStorage.getItem('role');
  url: any = [];

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

  ngOnInit(): void {
    if (localStorage.getItem('role') === 'FIELD_OWNER') {
      // @ts-ignore
      this.addForm.controls.email.setValue(localStorage.getItem('email'));
    }
  }
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

      this.sportCenterService.addSportCenter(formData).subscribe({
        next: () => {
          this.CloseForm();
        },
      });
    }
  }

  handleFileInput(event: any): void {
    let reader = new FileReader();
    reader.readAsDataURL(event.target.files[0]);
    const images = this.addForm.get('images') as FormArray;
    images.push(this.formBuilder.control(event.target.files[0]));
    reader.onload = (_event) => {
      this.url.push(reader.result);
    };
  }

  onDeleteImg(id: number) {
    this.url.splice(id, 1);
    const images = this.addForm.get('images') as FormArray;
    images.removeAt(id);
  }

  CloseForm() {
    if (this.role === 'ADMIN') {
      this.router.navigate(['/admin/sport-centers']).then(() => {
        window.location.reload();
      });
    } else {
      this.router.navigate(['/field-owner/home']).then(() => {
        window.location.reload();
      });
    }
  }
}
