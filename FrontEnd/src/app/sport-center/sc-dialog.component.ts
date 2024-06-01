import { Component, inject, ViewChild } from '@angular/core';
import { MAT_DIALOG_DATA, MatDialogRef } from '@angular/material/dialog';

@Component({
  selector: 'app-sc-dialog',
  template: `
    <div class="main-dialog">
      <div class="padding">
        <h1>Sportski centar {{ data.sportCenterName }}</h1>
        <p>
          Adresa: {{ data.streetAndNumber }}, {{ data.zipCode }}
          {{ data.cityName }} <br />
          Vlasnik: {{ data.owner }} <br />
        </p>
        <div class="flex center">
          <div class="carousel">
            <app-carousel [slides]="data.images"></app-carousel>
          </div>
        </div>
        <textarea
          #reasonMessage
          class="reason-input"
          placeholder="Ostavite poruku uz prihvaćanje ili odbijanje zahtjeva..."
        ></textarea>
        <div class="buttons">
          <button class="button reject" (click)="onNoClick()">Odbij</button>
          <button class="button" (click)="onYesClick()">Prihvati</button>
        </div>
      </div>
    </div>
  `,
  styles: [
    `
      .main-dialog {
        background-color: #323437;
        height: 100%;
        width: 100%;
      }
      .padding {
        padding: 20px;
      }
      .reason-input {
        font-size: 20px;
        border-radius: 10px;
        border: 1px solid #927979;
        background: transparent;
        margin-bottom: 20px;
        resize: none;
        height: 100px;
        width: 100%;
        color: #d1d0c5;

        &:focus {
          outline: none;
        }
      }
      button {
        margin: 10px;
      }
      .buttons {
        display: flex;
        justify-content: center;
      }
      p {
        font-size: 16px;
      }
      h1 {
        text-align: center;
      }
      .carousel {
        height: 270px;
        width: 360px;
        margin-bottom: 20px;
      }
      .reject {
        background-color: #ff3f3f;
        color: #d1d0c5;
      }
      .reject:hover {
        background-color: #b8bec5;
        color: #646669;
      }
    `,
  ],
})
export class ScDialogComponent {
  private dialogRef = inject(MatDialogRef<ScDialogComponent>);
  public data = inject(MAT_DIALOG_DATA);
  @ViewChild('reasonMessage') reasonMessage!: any;

  onNoClick(): void {
    const reasonMessage = this.reasonMessage.nativeElement.value;
    this.dialogRef.close(['REJECTED', reasonMessage]);
  }

  onYesClick(): void {
    const reasonMessage = this.reasonMessage.nativeElement.value;
    this.dialogRef.close(['ACTIVE', reasonMessage]);
  }
}
