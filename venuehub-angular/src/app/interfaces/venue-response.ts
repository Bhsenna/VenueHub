import { Address } from "./address";
import { RegisterRequest } from "./register-request";

export interface VenueResponse {

    id: number;
    nome: string;
    capacidade: number;
    descricao: string;
    telefone: string;
    valor: number;
    address: Address;
    user: RegisterRequest;
    ativo: boolean;
}
