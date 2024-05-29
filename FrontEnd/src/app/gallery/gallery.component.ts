import { Component, inject, OnInit } from '@angular/core';
import { MAT_DIALOG_DATA, MatDialogRef } from '@angular/material/dialog';
import { ReservationsComponent } from '../reservations/reservations.component';

@Component({
  selector: 'app-gallery',
  templateUrl: './gallery.component.html',
  styleUrl: './gallery.component.css',
})
export class GalleryComponent implements OnInit {
  private dialogRef = inject(MatDialogRef<ReservationsComponent>);
  public images = inject(MAT_DIALOG_DATA);
  public mainImage: any;

  ngOnInit() {
    this.mainImage = this.images[0];
  }

  changeMainImage(index: number) {
    this.mainImage = this.images[index];
  }

  closeGallery() {
    this.dialogRef.close();
  }
}
