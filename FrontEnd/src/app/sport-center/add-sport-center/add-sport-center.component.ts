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
  wrongAddress: boolean = false;

  addForm = this.formBuilder.nonNullable.group(
    {
      email: ['', [Validators.email]],
      sportCenterName: ['', [Validators.required]],
      streetAndNumber: ['', [Validators.required]],
      zipCode: ['', [Validators.required]],
      cityName: ['', [Validators.required]],
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
    if (this.addForm.valid && this.url.length > 0) {
      let formData = new FormData();
      let address =
        this.addForm.controls.streetAndNumber.value +
        ', ' +
        this.addForm.controls.zipCode.value +
        ' ' +
        this.addForm.controls.cityName.value;

      this.sportCenterService
        .getPosition(encodeURIComponent(address))
        .subscribe({
          next: (data: any) => {
            if (data.status == 'OK') {
              let lat = data.results[0].geometry.location.lat;
              let lng = data.results[0].geometry.location.lng;
              formData.append('latitude', lat);
              formData.append('longitude', lng);
              formData.append('email', this.addForm.controls.email.value);
              formData.append(
                'sportCenterName',
                this.addForm.controls.sportCenterName.value,
              );
              formData.append(
                'streetAndNumber',
                this.addForm.controls.streetAndNumber.value,
              );
              formData.append('zipCode', this.addForm.controls.zipCode.value);
              formData.append('cityName', this.addForm.controls.cityName.value);
              const images = this.addForm.get('images') as FormArray;
              for (
                let i = 0;
                i < this.addForm.controls.images.value.length;
                i++
              ) {
                formData.append('images', images.at(i).value);
              }

              this.sportCenterService.addSportCenter(formData).subscribe({
                next: () => {
                  this.CloseForm();
                },
                error: () => {
                  this.wrongAddress = true;
                },
              });
            }
          },
          error: (error) => {
            console.error('Error:', error);
            this.wrongAddress = true;
            return;
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
    event.target.value = null;
  }

  onDeleteImg(id: number) {
    this.url.splice(id, 1);
    const images = this.addForm.get('images') as FormArray;
    images.removeAt(id);
  }

  CloseForm() {
    this.router.navigate(['/sport-centers-list']).then(() => {
      window.location.reload();
    });
  }
}
