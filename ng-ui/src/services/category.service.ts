import { HttpClient } from '@angular/common/http';
import { Injectable } from '@angular/core';
import { categoryEntity } from '../models/category-entity';
import { subCategory } from '../models/category';
import { AsyncSubject } from 'rxjs';

@Injectable({
  providedIn: 'root'
})
export class AgencyService {

  private entities: Map<string, Array<subCategory>>;

  public onAgencyData: AsyncSubject<boolean> = new AsyncSubject<boolean>();

  constructor(private http: HttpClient) {
    this.entities = new Map<string, Array<subCategory>>();
    this.load();
  }

  public getAgencyData(): Map<string, Array<subCategory>> {
    return this.entities;
  }

  private load() {
    this.http.get("/agencies-categories").subscribe(data => {
      const ed: categoryEntity = data as categoryEntity;
      if (ed) {
        const edKeys = Object.keys(ed);
        edKeys.forEach(edKey => {
          this.entities.set(edKey, ed[edKey]);
        });
        this.onAgencyData.next(true);
        this.onAgencyData.complete();
      }
    });
  }
}
