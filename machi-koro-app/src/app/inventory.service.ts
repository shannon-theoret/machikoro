import { Injectable } from '@angular/core';
import { Observable, of, from} from 'rxjs';
import { CARDS } from './cards';

@Injectable({
  providedIn: 'root'
})
export class InventoryService {

  constructor() { }

  getCards(): Observable<any[]>{
    return of(CARDS);
  }

  getColumns(): string[] {
    return [ "roll", "card", "result", "cost"]
  }
}
