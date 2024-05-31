import { Component, inject, OnInit } from '@angular/core';
import { MAT_DIALOG_DATA, MatDialogRef } from '@angular/material/dialog';
import { FieldReservationsComponent } from '../reservations/field-reservations/field-reservations.component';

@Component({
  selector: 'app-gallery',
  templateUrl: './gallery.component.html',
  styleUrl: './gallery.component.css',
})
export class GalleryComponent implements OnInit {
  private dialogRef = inject(MatDialogRef<FieldReservationsComponent>);
  public data = inject(MAT_DIALOG_DATA);
  public mainImage: any;
  public images: any;
  public description: any;

  ngOnInit() {
    this.images = this.data[0];
    this.mainImage = this.images[0];
    this.description = this.data[1];
  }

  changeMainImage(index: number) {
    this.mainImage = this.images[index];
  }

  closeGallery() {
    this.dialogRef.close();
  }
}
