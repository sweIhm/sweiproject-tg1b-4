import { Component, OnInit } from '@angular/core';

@Component({
  selector: 'app-activity-overview',
  templateUrl: './activity-overview.component.html',
  styleUrls: ['./activity-overview.component.css']
})
export class ActivityOverviewComponent implements OnInit {

  constructor() { }

  ngOnInit() {
  }

  activity = {
    id: 1,
    title: 'Activity',
    content: 'test',
    tag: 'tag'
  }
}
