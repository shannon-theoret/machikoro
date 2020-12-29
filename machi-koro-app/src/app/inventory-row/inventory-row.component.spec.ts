import { async, ComponentFixture, TestBed } from '@angular/core/testing';

import { InventoryRowComponent } from './inventory-row.component';

describe('InventoryRowComponent', () => {
  let component: InventoryRowComponent;
  let fixture: ComponentFixture<InventoryRowComponent>;

  beforeEach(async(() => {
    TestBed.configureTestingModule({
      declarations: [ InventoryRowComponent ]
    })
    .compileComponents();
  }));

  beforeEach(() => {
    fixture = TestBed.createComponent(InventoryRowComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
