import {Component, Input, OnInit} from '@angular/core';
import {Player} from "../app.component";
import {Observable} from "rxjs";
import {InventoryService} from "../inventory.service";

@Component({
  selector: 'app-player',
  templateUrl: './player.component.html',
  styleUrls: ['./player.component.css']
})
export class PlayerComponent implements OnInit {

  @Input() player : Player;
  @Input() prevPlayer : Player;
  cards: Observable<any[]>;

  constructor(private inventoryService: InventoryService) { }

  ngOnInit() {
    this.cards = this.inventoryService.getCards();
  }

}
