import { HttpClient } from '@angular/common/http';
import { Injectable } from '@angular/core';
import { record } from '../models/record';
import { catchError, map, of, Subject } from 'rxjs';

@Injectable({
  providedIn: 'root'
})
export class RecordService {

  private records: Array<record>;

  public onGetRecord: Subject<Array<record>> = new Subject<Array<record>>();
  public onSave: Subject<boolean> = new Subject<boolean>();
  public onDelete: Subject<boolean> = new Subject<boolean>();

  constructor(private http: HttpClient) {
    this.records = new Array<record>();
  }

  public getRecord(id: number): void {
    const sub = this.http.get(`/api/records/${id}`).pipe(
      map(d => d),
      catchError((err, caught) => of([]))
    ).subscribe(data => {
      if (sub) {
        sub.unsubscribe();
      }
      this.records = data as Array<record>;
      this.onGetRecord.next(this.records);
    });
  }

  public setRecord(rec: record): void {
    const sub = this.http.post(`/api/records`, rec).pipe(
      map(d => d),
      catchError((err, caught) => of(null))
    ).subscribe(data => {
      if (sub) {
        sub.unsubscribe();
      }
      if (data) {
        this.onSave.next(true);
      } else {
        this.onSave.next(false);
      }
    });
  }

  public delRecord(id: number): void {
    const sub = this.http.delete(`/api/records/${id}`).pipe(
      map(d => true),
      catchError((err, caught) => of(false))
    ).subscribe(data => {
      if (sub) {
        sub.unsubscribe();
      }
      if (data) {
        this.onDelete.next(true);
      } else {
        this.onDelete.next(false);
      }
    });
  }

}
