import { Address } from "./address";

export interface RegisterRequest {
  nome: string;
  sobrenome: string;
  login: string;
  senha: string;
  role: string;
  ativo: boolean;
  address: Address;
}