import { Component, OnInit, Input } from '@angular/core';
import {Observable} from "rxjs";
import {InventoryService} from "../inventory.service";
import {AppComponent, Stock} from "../app.component";


@Component({
  selector: 'app-inventory',
  templateUrl: './inventory.component.html',
  styleUrls: ['./inventory.component.css']
})
export class InventoryComponent implements OnInit {

  @Input() stock: Stock;
  cards: Observable<any[]>;
  columns: string[];

  constructor(private inventoryService: InventoryService, private appComponent: AppComponent) {}

  ngOnInit() {
    this.cards = this.inventoryService.getCards();
    this.columns = this.inventoryService.getColumns();
  }

  purchaseCard(card) {
    this.appComponent.purchseCard(card)
  }

}
