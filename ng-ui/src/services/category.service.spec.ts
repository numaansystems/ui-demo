import { TestBed } from '@angular/core/testing';

import { AgencyService } from './category.service';

describe('AgencyService', () => {
  let service: AgencyService;

  beforeEach(() => {
    TestBed.configureTestingModule({});
    service = TestBed.inject(AgencyService);
  });

  it('should be created', () => {
    expect(service).toBeTruthy();
  });
});
