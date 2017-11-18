import { Component, OnInit } from '@angular/core';

@Component({
  selector: 'app-activity-details',
  templateUrl: './activity-details.component.html',
  styleUrls: ['./activity-details.component.css']
})
export class ActivityDetailsComponent implements OnInit {

  activity = {
    id: 1,
    title: 'Activity',
    content: 'test',
    tag: 'tag'
  }

  constructor() { }

  ngOnInit() {
  }

}
