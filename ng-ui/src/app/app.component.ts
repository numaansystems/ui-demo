import { Component, OnDestroy, OnInit } from '@angular/core';
import { RouterOutlet } from '@angular/router';
import { SplitterModule } from 'primeng/splitter';
import { CommonModule } from '@angular/common';
import { BrowserAnimationsModule, NoopAnimationsModule } from '@angular/platform-browser/animations';
import { TreeModule } from 'primeng/tree';
import { MessageService, TreeNode } from 'primeng/api';
import { AgencyService } from '../services/category.service';
import { Subscription } from 'rxjs';
import { record } from '../models/record';
import { TableModule } from 'primeng/table';
import { DividerModule } from 'primeng/divider';
import { DialogModule } from 'primeng/dialog';
import { RecordService } from '../services/record.service';
import { FontAwesomeModule } from '@fortawesome/angular-fontawesome';
import {
  faSquareXmark,
  faPenToSquare
} from '@fortawesome/free-solid-svg-icons';
import { ButtonModule } from 'primeng/button';
import { InputTextModule } from 'primeng/inputtext';
import { FloatLabelModule } from 'primeng/floatlabel';
import { FormsModule } from '@angular/forms';
import { ToastModule } from 'primeng/toast';

@Component({
  selector: 'app-root',
  standalone: true,
  imports: [RouterOutlet, SplitterModule, CommonModule, TreeModule, TableModule, FontAwesomeModule,
    DividerModule, ButtonModule, DialogModule, InputTextModule, FloatLabelModule, FormsModule, ToastModule],
  providers: [AgencyService, RecordService, MessageService],
  templateUrl: './app.component.html',
  styleUrl: './app.component.css'
})
export class AppComponent implements OnInit, OnDestroy {
  private agencyDataSub?: Subscription;
  private recordDataSub?: Subscription;
  private saveSub?: Subscription;
  private delSub?: Subscription;

  title = 'UI Demo';
  agencies: TreeNode[] = [];
  recs: record[] = [];
  editIcon = faPenToSquare;
  delIcon = faSquareXmark;
  visible = false;
  selectedRecord?: TreeNode;
  newRecord: record = { name: "", department: "", city: "", id: 0 };
  saveError = false

  constructor(private agencyService: AgencyService, private recService: RecordService, private msgService: MessageService) {

  }

  ngOnInit(): void {
    this.agencyDataSub = this.agencyService.onAgencyData.subscribe(ready => {
      if (ready) {
        const data = this.agencyService.getAgencyData();
        const ags: TreeNode[] = [];
        for (let [k, v] of data) {
          const ptn: TreeNode = { label: k, key: k, children: [] };
          ptn.expanded = true;
          ptn.selectable = false;
          ptn.children = v.map(v1 => {
            return ({ label: v1.subCategory, data: v1, key: v1.id }) as any;
          });
          ags.push(ptn);
        }
        this.agencies = ags;
      }
    });
    this.recordDataSub = this.recService.onGetRecord.subscribe(data => {
      this.recs = data;
    });
    this.saveSub = this.recService.onSave.subscribe(flag => {
      if (true === flag) {
        this.msgService.add({ severity: 'success', summary: 'Success', detail: 'Record created successfully.' });
        this.visible = false;
        this.refresh();
        this.newRecord.name = "";
        this.newRecord.department = "";
        this.newRecord.city = "";
      } else {
        //this.saveError = true;
        this.msgService.add({ severity: 'error', summary: 'Error', detail: 'Unable to save. Please try again!', life: 5000 });
      }
    });
    this.delSub = this.recService.onDelete.subscribe(flag => {
      if (true === flag) {
        this.msgService.add({ severity: 'success', summary: 'Success', detail: 'Record deleted successfully.' });
        this.refresh();
      } else {
        this.msgService.add({ severity: 'error', summary: 'Error', detail: 'Unable to delete. Please try again!', life: 5000 });
      }
    });
  }
  ngOnDestroy(): void {
    if (this.agencyDataSub) {
      this.agencyDataSub.unsubscribe();
    }
    if (this.recordDataSub) {
      this.recordDataSub.unsubscribe();
    }
    if (this.saveSub) {
      this.saveSub.unsubscribe();
    }
    if (this.delSub) {
      this.delSub.unsubscribe();
    }
  }
  get canCreateNew(): boolean {
    return this.selectedRecord !== undefined;
  }
  createNew(): void {
    this.visible = true;
    this.newRecord.id = this.selectedRecord?.data.id;
  }
  refresh(): void {
    this.recService.getRecord(this.selectedRecord?.data.id);
  }
  saveNew(): void {
    this.recService.setRecord(this.newRecord);
  }
  delete(id: number): void {
    this.recService.delRecord(id);
  }
  nodeSelect(event: any) {
    this.selectedRecord = event.node;
    this.refresh();
  }

  nodeUnselect(event: any) {
    this.selectedRecord = undefined;
    this.recs = [];
  }
}
