# FeirApp-P3 - Proposta

Projeto da disciplina IF1001 - Programação 3 (Programação para Dispositivos Móveis com Android)

## Justificativa

O hábito de fazer feira já é tradição no Brasil, quase todas as pessoas, tanto as que moram sozinhas quanto as que fazem parte de uma família costumam fazer. Alem de que realizar feiras mensais economizam tempo e muitas vezes até dinheiro. 

Pensando nisso, no limite de dinheiro que a maioria das pessoas tem para gastar mensalmente e em como otimizar o processo de realizar uma lista de compras até ir fazer a feira resolvemos criar um auxiliar de feiras. Que diferente de outros aplicativos existentes - onde as interfaces são lotadas de informação e você perde tempo digitando coisas desnecessárias para cada produto que deseja - pensamos em algo simples, intuitivo, útil e eficiente. 

O App terá ma interface simples de criação de listas de compras otimizada para rápida adição de itens, e um carrinho de compras que é utilizado no momento das compras, onde é digitado quantidade e preço de cada produto. O carrinho serve para mantermos o controle dos gastos em tempo real e o histórico para análises futuras.

Além disso, será possível importar, exportar e compartilhar listas de compras com outras pessoas.

## Publico Alvo

Todas as pessoas que costumam fazer feiras ou listas de compras.

## Concorrentes

* SoftList;
* Bring;
* Apps de Lista de Compras. 

## Proposta de Valor

O nosso App se destaca na simplicidade, facilidade de uso e na proposta de fornecer em tempo real se os produtos comprados já atingiram algum limite estipulado.

## Estrutura

A aplicação será estruturada, inicialmente, em duas telas/abas, no qual se concentrará todas as features. Essas telas são denominadas de Lista e Carrinho, no qual o usuário poderá ver e interagir com os produtos adicionados ou selecionados para compra.

- Aba Inicial (Produtos): Listview dos produtos adicionados com uma checkbox para adicionar ao carrinho. 
- Carrinho: Listview dos produtos selecionados na tab inicial.
- Checkbox (Tab inicial): Usado para mediar a inserção no carrinho, podendo descrever algumas caractersticas do produto (preço e quantidade) por meio de um pop-up.
- Footer: Usado na Aba Inicial para adicionar um novo produto, similarmente utilizado no Carrinho com a diferença que no carrinho é possível adicionar diretamente sem passar pelo pop-up.
- Sidebar: Menu escondido que é acionado apertando o action overflow icon que possui links importantes como: configurações (preferências), termos de uso e etc.

> Fluxo exemplo: Inserir um novo produto na aba inicial através de um input. Dar checked no produto adicionado. Escolher a quantidade que deseja comprar com o respectivo preço unitário (pressionar OK). Ir no carrinho e verificar o preço total dos produtos. 

O mockup do projeto se encontra [aqui](https://drive.google.com/file/d/1gT7d-w-mxrgKVlOLT6VH32_x9dm0U7Sj/view?usp=sharing).

## Equipe e Divisão de Tarefas

| Aluno            | Designações Principais |
| --------         | -------- |
| Joismar Braga    | Idealização inicial e desenvolvimento. |
| Jean Carlos      | Mockup, prototipação e desenvolvimento.     |


