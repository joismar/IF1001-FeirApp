## IF1001-FeirApp
Projeto da disciplina IF1001 - Programação 3 (Programação para Dispositivos Móveis com Android - Kotlin)

#### Equipe:

- Jean Carlos
- Joismar Antonio

#### Objetivo
Dar ao usuário a possibilidade de utilizar uma calculadora moderna para organizar suas compras, de forma que sempre saibam o valor e itens que estão adquirindo.

#### Páginas e Funcionalidades Criadas

- Carrinho (Listagem de produtos com preço e quantidade)
- Lista de Compra (Listagem de produtos)
- Compartilhamento (Uma tentativa de compartilhar com outro usuário para edição em tempo real e/ou offline)

#### Banco de Dados

- Memória Interna do Dispositivo
- [Firebase](https://firebase.google.com/) (Deveria ser utilizado para o compartilhamento e persistência dos dados do usuário, mas não ficou finalizado)

#### Layouts

- LinearLayout
- RelativeLayout
- ScrollView

#### Descrição

O FeirApp é um projeto mobile, desenvolvido em Kotlin, que surgiu da necessidade de organizar melhor as compras, inicialmente no Supermercado, em um único local, capaz de mostrar os produtos, contabilizar o valor e compartilhar com usuários e não usuários da aplicação. O usuário através de uma interface intuitiva, poderá adicionar seus produtos à sua lista de compras e posteriormente ao carrinho para contabilizar o total por produto (caso adicione mais de uma unidade daquele produto) e o total geral.

A aplicação foi dividia em Carrinho e Lista de Produtos, dois adapters, que possuem seus inputs (classe Produto) independentes. Os inputs são inseridos em um array que é mostrado de modos diferentes pelas duas telas. Além disso foi utilizado dois dialogs, um para passar dados da lista de produtos para o array do carrinho, quanto pra abrir buttons de ações de edição/remoção do produto do carrinho.

Foi iniciada em um momento da aplicação, uma feature de compartilhamento de dados internamente pela rede de internet. Não foi conluído, porém está em estágio inicials e se encontra na versão criada da aplicação.
