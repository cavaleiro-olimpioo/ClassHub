import { createContext, useContext, useState } from 'react';
import { avisosIniciais, ocorrenciasIniciais, itensIniciais } from '../data/mockData';
const AppContext = createContext();
export function AppProvider({ children }) { const [avisos,setAvisos]=useState(avisosIniciais),[ocorrencias,setOcorrencias]=useState(ocorrenciasIniciais),[itens,setItens]=useState(itensIniciais),[toast,setToast]=useState(null); const notify=(mensagem,tipo='sucesso')=>{setToast({mensagem,tipo});setTimeout(()=>setToast(null),3000)}; return <AppContext.Provider value={{avisos,setAvisos,ocorrencias,setOcorrencias,itens,setItens,toast,notify}}>{children}</AppContext.Provider> }
export const useApp = () => useContext(AppContext);
