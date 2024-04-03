import { Component, inject, OnInit } from '@angular/core';
import { FormArray, FormBuilder, Validators } from '@angular/forms';
import { ActivatedRoute, Router } from '@angular/router';
import { SportCenterService } from '../sport-center.service';
import { ConfirmDialogComponent } from './confirm-dialog.component';
import { MatDialog } from '@angular/material/dialog';

@Component({
  selector: 'app-add-sport-center',
  templateUrl: './edit-sport-center.component.html',
  styleUrl: './edit-sport-center.component.css',
})
export class EditSportCenterComponent implements OnInit {
  private formBuilder = inject(FormBuilder);
  private sportCenterService = inject(SportCenterService);
  private router = inject(Router);
  private route = inject(ActivatedRoute);
  private dialog = inject(MatDialog);

  private oldData: any;
  private imagesChanged: boolean = false;
  protected addFieldForm: boolean = false;
  protected role = localStorage.getItem('role');
  url: any = [];
  sportCenterId: any;
  fields: any;
  editingField: any = null;

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
    this.route.params.subscribe((params) => {
      this.sportCenterId = params['id'];
      this.sportCenterService.getSportCenterById(this.sportCenterId).subscribe({
        next: (data: any) => {
          this.oldData = data;
          this.addForm.controls.email.setValue(data.owner);
          this.addForm.controls.sportCenterName.setValue(data.sportCenterName);
          this.addForm.controls.address.setValue(data.address);
          for (let i of data.images) {
            this.url.push('data:image/png;base64,' + i);
          }
          const images = this.addForm.get('images') as FormArray;
          for (let i of data.images) {
            images.push(
              this.formBuilder.control(
                this.base64toFile(
                  'data:image/png;base64,' + i,
                  'picture.jpg',
                  'image/jpeg',
                ),
              ),
            );
          }
        },
      });
    });
    this.sportCenterService.getSportCenterFields(this.sportCenterId).subscribe({
      next: (data: any) => {
        this.fields = data;
      },
    });
  }

  base64toFile(base64String: string, fileName: string, mimeType: string): File {
    const base64Data = base64String.split(',')[1];
    const binaryString = atob(base64Data);
    const byteArray = new Uint8Array(binaryString.length);
    for (let i = 0; i < binaryString.length; i++) {
      byteArray[i] = binaryString.charCodeAt(i);
    }
    const blob = new Blob([byteArray], { type: mimeType });
    return new File([blob], fileName, { type: mimeType });
  }

  editSportCenter() {
    if (this.addForm.valid) {
      if (
        this.oldData.sportCenterName ===
          this.addForm.controls.sportCenterName.value &&
        this.oldData.address === this.addForm.controls.address.value &&
        !this.imagesChanged
      ) {
        this.CloseForm();
      } else {
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
        this.sportCenterService
          .editSportCenter(formData, this.sportCenterId)
          .subscribe({
            next: () => {
              this.CloseForm();
            },
          });
      }
    }
  }

  handleFileInput(event: any): void {
    this.imagesChanged = true;
    let reader = new FileReader();
    reader.readAsDataURL(event.target.files[0]);
    const images = this.addForm.get('images') as FormArray;
    images.push(this.formBuilder.control(event.target.files[0]));
    reader.onload = (_event) => {
      this.url.push(reader.result);
    };
  }

  onDeleteImg(id: number) {
    this.imagesChanged = true;
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

  deleteSportCenter() {
    const dialogRef = this.dialog.open(ConfirmDialogComponent, {
      data: 'sportski centar',
    });
    dialogRef.afterClosed().subscribe((reason: any) => {
      if (reason) {
        this.sportCenterService
          .deleteSportCenter(this.sportCenterId, reason)
          .subscribe({
            next: () => {
              this.CloseForm();
            },
          });
      }
    });
  }
  addField() {
    this.addFieldForm = !this.addFieldForm;
    this.editingField = null;
  }

  editField(fieldId: any) {
    this.editingField = fieldId;
    this.addFieldForm = false;
  }

  closeAddFieldForm(fieldName: any) {
    if (fieldName) {
      this.fields = this.fields.filter((f: any) => f.fieldName !== fieldName);
    }
    this.addFieldForm = false;
    this.editingField = null;
  }
}
