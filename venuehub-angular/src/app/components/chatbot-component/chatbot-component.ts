import { Component, AfterViewInit, CUSTOM_ELEMENTS_SCHEMA } from '@angular/core';

declare var Typebot: any;

@Component({
  selector: 'app-chatbot',
  template: `
    <typebot-standard style="width: 100%; height: 600px;"></typebot-standard>
  `,
   schemas: [CUSTOM_ELEMENTS_SCHEMA]
})
export class ChatbotComponent implements AfterViewInit {
  ngAfterViewInit() {
    if (Typebot) {
      Typebot.initStandard({ typebot: "my-typebot-erd4xfv",     
        apiHost: "http://10.1.22.2:3002", });
    }
  }
}
