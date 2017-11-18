/**
 * Created by rottc on 18/11/2017.
 */

import { NgModule } from '@angular/core';
import { RouterModule, Routes } from '@angular/router';
import {ActivityOverviewComponent} from './activities/activity-overview.component';
import {RegistrationComponent} from './registration/registration.component';
import {LoginComponent} from './login/login.component'
import {ActivityDetailsComponent} from './activity-details/activity-details.component';

/*
 Routes in the application.
 */
const ROUTES: Routes = [
  { path: "", redirectTo: "/activities", pathMatch: "full" },
  { path: "activities", component: ActivityOverviewComponent },
  { path: "activities_detail/id", component: ActivityDetailsComponent },
  { path: "registration", component: RegistrationComponent },
  { path: "login", component: LoginComponent}
]

@NgModule({
  exports: [ RouterModule ],
  imports: [ RouterModule.forRoot(ROUTES) ]
})

export class AppRoutingModule {}
