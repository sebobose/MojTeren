import { Component, inject, ViewChild } from '@angular/core';
import { MAT_DIALOG_DATA, MatDialogRef } from '@angular/material/dialog';
import { ReservationService } from '../reservation.service';

@Component({
  selector: 'app-make-reservation-dialog',
  template: `
    <div class="main-dialog">
      <div class="padding">
        <h1>Potvrda rezervacije</h1>
        <p>
          Sportski centar: {{ data.sportCenterName }} <br />
          Teren: {{ data.fieldName }} <br />
          Datum: {{ data.date }} <br />
          Početak: {{ data.startTime }} <br />
          Kraj: {{ data.endTime }} <br />
        </p>
        @if (role != 'ATHLETE') {
          <textarea
            #user
            class="user-input"
            placeholder="Unesite mail korisnika"
          ></textarea>
          @if (userError) {
            <span class="form-error"
              >Morate unijeti ispravan mail korisnika!</span
            >
          }
        }
        <textarea
          #reservationMessage
          class="reservation-message-input"
          placeholder="Ostavite poruku uz rezervaciju"
        ></textarea>
        <div class="buttons">
          <button class="button" (click)="onNoClick()">Odustani</button>
          <button class="button" (click)="onYesClick()">Potvrdi</button>
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
      .reservation-message-input {
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
      .user-input {
        font-size: 20px;
        border-radius: 10px;
        border: 1px solid #927979;
        background: transparent;
        margin-bottom: 10px;
        resize: none;
        height: 30px;
        width: 100%;
        color: #d1d0c5;

        &:focus {
          outline: none;
        }
      }
      .form-error {
        margin: 10px;
      }
    `,
  ],
})
export class MakeReservationDialogComponent {
  private dialogRef = inject(MatDialogRef<MakeReservationDialogComponent>);
  private reservationService = inject(ReservationService);

  public data = inject(MAT_DIALOG_DATA);
  public role = localStorage.getItem('role') || '';
  public userError = false;
  @ViewChild('reservationMessage') reservationMessage!: any;
  @ViewChild('user') user!: any;

  onNoClick(): void {
    this.dialogRef.close();
  }

  onYesClick(): void {
    const reservationMessage = this.reservationMessage.nativeElement.value;
    if (this.role === 'FIELD_OWNER') {
      let user = this.user.nativeElement.value;
      if (user === '') {
        this.userError = true;
        return;
      }
      this.reservationService.checkUser(user).subscribe({
        next: () => {
          this.dialogRef.close(['yes', reservationMessage, user]);
        },
        error: (error) => {
          console.error('Error:', error);
          this.userError = true;
        },
      });
    } else {
      this.dialogRef.close([
        'yes',
        reservationMessage,
        localStorage.getItem('email'),
      ]);
    }
  }
}
