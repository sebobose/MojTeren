import { Component, inject, OnInit, ViewChild } from '@angular/core';
import { MAT_DIALOG_DATA, MatDialogRef } from '@angular/material/dialog';
import { ReservationService } from '../reservation.service';

@Component({
  selector: 'app-reservation-details-dialog',
  template: `
    <div class="main-dialog">
      <div class="padding">
        <div class="close-button">
          <mat-icon (click)="closeDialog()">close</mat-icon>
        </div>
        <h1>Detalji rezervacije</h1>
        <p>
          Ime i prezime: {{ data.name }} <br />
          Email: {{ data.email }} <br />
          Telefon: {{ data.contact }} <br />
          Sportski centar: {{ data.sportCenterName }} <br />
          Teren: {{ data.fieldName }} <br />
          Datum: {{ data.date }} <br />
          Početak: {{ data.startTime }} <br />
          Kraj: {{ data.endTime }} <br />
          Cijena: {{ data.price }} € <br />
          {{ data.message }}
        </p>
        <h1>Želite li otkazati rezervaciju?</h1>
        <textarea
          #reservationMessage
          class="reservation-cancel-input"
          placeholder="Zašto otkazujete rezervaciju?"
        ></textarea>
        <div class="buttons">
          <button class="button" (click)="closeDialog()">Odustani</button>
          <button class="button delete-button" (click)="onYesClick()">
            Otkaži rezervaciju
          </button>
        </div>
        @if (inputError) {
          <span class="form-error">Morate unijeti razlog otkazivanja!</span>
        }
      </div>
    </div>
  `,
  styles: [
    `
      .main-dialog {
        background-color: #323437;
        height: 200%;
        width: 100%;
      }
      .padding {
        padding: 20px;
      }
      .reservation-cancel-input {
        font-size: 20px;
        border-radius: 10px;
        border: 1px solid #927979;
        background: transparent;
        margin-bottom: 20px;
        resize: none;
        height: 100px;
        width: 100%;
        color: #d1d0c5;
        margin-top: 10px;

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
      .form-error {
        margin: 10px;
      }
      .close-button {
        position: absolute;
        top: 7px;
        right: 10px;
        font-size: 30px;
        color: #d1d0c5;
        cursor: pointer;
      }
      .delete-button {
        background-color: #ff3f3f;
        color: #d1d0c5;
      }

      .delete-button:hover {
        background-color: #b8bec5;
        color: #646669;
      }
    `,
  ],
})
export class ReservationDetailsDialog implements OnInit {
  private dialogRef = inject(MatDialogRef<ReservationDetailsDialog>);

  public data = inject(MAT_DIALOG_DATA);
  public role = localStorage.getItem('role') || '';
  public inputError = false;
  @ViewChild('reservationMessage') reservationMessage!: any;

  ngOnInit() {
    if (this.data.contact == '') {
      this.data.contact = 'Nije unesen';
    }
    if (this.data.message != undefined || this.data.message != '') {
      this.data.message = 'Poruka uz rezervaciju: ' + this.data.message;
    }
  }

  onYesClick(): void {
    const reservationMessage = this.reservationMessage.nativeElement.value;
    if (reservationMessage === '') {
      this.inputError = true;
      return;
    }
    this.dialogRef.close(['yes', reservationMessage]);
  }

  closeDialog() {
    this.dialogRef.close();
  }
}
