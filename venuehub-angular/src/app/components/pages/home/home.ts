import { Component } from '@angular/core';
import { VenueList } from "../../list/venue-list/venue-list";

@Component({
  selector: 'app-home',
  imports: [VenueList],
  templateUrl: './home.html',
  styleUrl: './home.css'
})
export class Home {

}
