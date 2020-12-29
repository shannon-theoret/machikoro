import {Component, Input, OnInit} from '@angular/core';
import {Dice} from "../app.component";

@Component({
  selector: 'app-dice',
  templateUrl: './dice.component.html',
  styleUrls: ['./dice.component.css']
})
export class DiceComponent implements OnInit {

  @Input() recentRoll: Dice;

  constructor() { }

  ngOnInit() {
  }

}
