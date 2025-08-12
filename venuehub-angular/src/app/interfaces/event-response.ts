export interface EventResponse {
  id: number;
  tipoEvento: string;
  qtPessoas: number;
  dataInicio: string;
  dataFim: string;
  horaInicio: string;
  horaFim: string;
  status: string;
  venueId: number;
  user: any; 
  additionals: any[];
}
