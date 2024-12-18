package com.github.dearrudam.java_studies_oop.session_01;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class Generics {

    public static void main(String[] args) {

        /**
         * Generics foi adicionado a linguagem Java desde a versão 5, e ela traz grandes benefícios ao desenvolvimento
         * com a linguagem em termos de segurança, legibilidade e reutilização de código.
         *
         *
         *
         * a utilização da palavra reservada "final" é muito importante para ajudar a não deixarmos ninguém cair na
         * armadilha de mudar o valor/referência de uma váriavel. Para variáveis e argumentos que desejamos que ninguém
         * altere seu valor/referência, utilize "final".
         *
         * A utilização da palavra reservada "var" permite a inferencia de tipos no Java.
         *
         * Claro, tem desenvolvedores que preferem utilizar uma declaração mais verbosa como:
         *
         * ArrayList<String> minhaLista =  new ArrayList<String>();
         *
         * ou
         *
         * ArrayList<String> minhaLista = new ArrayList<>();
         *
         * Mas no fim, a compreensão e leitura do código é um item que devemos considerar!
         *
         * Suponhamos que temos a oportunidade de utilizar a palavra "var", mas para um objeto que é resultado de uma
         * execução de método, por exemplo:
         *
         *
         * var meuResult = algumMetodoIncrivel();
         *
         * Olhando para essa instrução não é possível detectar o tipo do objeto meuResult facilmente, isto é, ou vocẽ
         * utiliza a IDE (IntelliJ, Eclipse, NetBeans, VSCode com plugin para Java, entre outros) para saber, ou você precisará navegar
         * até o método "algumMetodoIncrivel()" para ver qual é o tipo que está retornado.
         *
         * No fim, precisamos sempre ponderar como escreveremos nossos códigos, pois amanhã alguém, incluindo
         * você mesmo, precisará revisar ou dar manutenção para esse código.
         *
         * Estamos utilizando aqui uma instância de "ArrayList" para instanciar a variável "minhaLista", mas essa seria
         * a melhor implementação que devemos utilizar?
         *
         * É comum entrarmos nessa questão e minha experiência mostra que a resposta depende do contexto em que estamos
         * inseridos. Talvez perguntas como as seguintes podem nos ajudar a tomar a melhor decisão possível:
         *
         *  - Precisamos nos preocupar com a utilização de memória e performance?
         *  - Iremos adicionar, pesquisar e remover itens da lista com frequência na lista?
         *  - A lista tem um tamanho já esperado, ou ele é dinâmico?
         *  - Precisaremos ficar percorrendo os itens na lista em questão para realizar algum processamento?
         *
         * Dependendo do contexto, talvez a utilização de um ArrayList não seja adequado! Por isso é importante conhecer
         * o funcionamento das Collections fornecidos pela API do Java. Além disso há bibliotecas também que fornecem
         * alternativas e outras implementações para estruturas de dados que a área da computação pode oferecer, mas
         * assumir uma biblioteca traz outras questões que podemos abordar em outro momento. Por hora, recomendo dar
         * preferência para as implementações fornecidas pela API padrão do Java.
         *
         * Para o nosso exemplo, utilizei ArrayList, mas os itens abaixo me trouxeram uma solução quanto aos trade-offs
         * para tal utilização:
         *
         *  - Não vou precisar me preocupar com memória no momento, pois o código é para estudo!
         *  - Tenho o número exato de itens que a lista irá manter, que é "5";
         *  - Não vou precisar adicionar, pesquisar ou remover itens por hora;
         *
         * Essas respostas me mostraram que utilizar um ArrayList com o "initialCapacity" 5 resolveria nossa questão. Mas
         * isso não quer dizer que essa é a resposta definitiva! Poderia ter delegado isso para o método fábrica:
         *
         *      List.of(
         *          "objeto 1",
         *          "objeto 2",
         *          "objeto 3",
         *          "objeto 4",
         *          "objeto 5"
         *      );
         *
         * Mas, para mim, usar ArrayList aparentou ser uma solução plausível para o contexto em questão! Caso o contexto
         * mude, essa decisão talvez deverá ser revista.
         *
         * NÃO EXITE BALA DE PRATA!!!
         *
         * A resposta DEPENDE só é aceita caso você não tenha a visão e conhecimento do contexto em questão.
         *
         * Uma vez que vc tem o contexto, a resposta DEPENDE não deveria ser satisfatória! E saber a resposta para um
         * determinado contexto é necessário análise e estudo, então, dizer que não sabe não é um problema desde que
         * a busca pela solução esteja já endereçada, através de pesquisas e estudos.
         *
         **/

        final var minhalista = new ArrayList<String>(5);
        minhalista.add("objeto 1");
        minhalista.add("objeto 2");
        minhalista.add("objeto 3");
        minhalista.add("objeto 4");
        minhalista.add("objeto 5");

        final var minhaListaSemGenerics = new ArrayList();
        populateListaSemGenerics(minhaListaSemGenerics);


        var lista2 = regraSemGenerics(null);
        System.out.println("minha lista depois regraB " + minhalista);
    }


    /**
     * Implicitamente, toda interface java que possui apenas um método abstrato que necessite ser implementado é
     * considerada uma interface funcional.
     * <p>
     * A anotação (ou annotation) @FunctionalInterface é opcional, mas é uma boa prática utilizá-la para instruir o
     * compilador a garantir que apenas um método abstrato que necessite de implementação exista na interface
     */
    @FunctionalInterface
    public static interface MinhaInterface {
        /**
         * Limitar a interface de um objeto, dependendo do contexto, pode ser uma boa prática
         * pois evita que o objeto seja utilizado de forma incorreta
         */
        String[] getAllItems();
    }

    private static List regraSemGenerics(List minhaLista) {
        List lista2 = new ArrayList();
        lista2.addAll(minhaLista);
        return lista2;
    }

    private static List<String> regraComGenericsFixos(List<String> minhalista) {

        List<String> lista2 = new ArrayList<>();
        lista2.addAll(minhalista);

        /**
         * Lembre-se: Todo objeto expõe seus métodos através da sua interface! Entenda aqui que interface não é o tipo
         * de classe que utiliza a palavra reservada "interface", mas sim os métodos públicos e protegidos que a classe
         * do objeto expõe.
         *
         * Métodos públicos são acessíveis por qualquer classe de qualquer pacote, entretanto, os
         * métodos protegidos são aqueles somente acessíveis pelas classes de seu package.
         *
         * Já os métodos privados são acessíveis somente dentro da classe que os implementam
         *
         * Abaixo, a interface List<String> do argumento minhaLista permite que chamem métodos que talvez não sejam
         * interessante no ponto de vista da execução do método e seu propósito, por isso, dependendo do próposito do
         * objetivo e propósito do método, essa abordagem não seja adequada.
         *
         * Em teoria, acesso a esses métodos podem permitir que quem estiver implementando o método a realizar operações
         * que não deveriam ser executadas deixando o sistema em um estado inconsistente e propenso a BUGS que podem ser
         * difíceis de detectar e reproduzir. Contudo, há abordagem que podem ajudar a mitigar esses cenários, como a
         * passagem de cópias do objeto que está sendo passado no método ou, ao invés de passar o objeto real, passar a
         * referência através de uma interface limitada na qual só permita os métodos que realmente devem ser expostos.
         **/
        minhalista.clear();

        return minhalista;
    }

    /**
     * Essa abordagem permite uma flexibilidade interessante! Esse método aceita listas de qualquer tipo!
     */
    private static <T>List<T> regraComGenericsDinamicos(List<T> minhalista) {
        var lista2 = new ArrayList<T>();
        lista2.addAll(minhalista);
        return lista2;
    }

    /**
     * Nesse exemplo, ao invés de passarmos a instância de List<String> do argumento minhaLista, estamos passando a
     * referencia de uma instância do tipo MinhaInterface, fornecendo apenas os métodos que eu, como desenvolvedor, desejo
     * que possa serem invocados dentro do método.
     */
    private static List<String> regraB(MinhaInterface minhalista) {

        List<String> lista2 = new ArrayList<>();
        lista2.addAll(Arrays.asList(minhalista.getAllItems()));
        return lista2;
    }

    /**
     *
     */
    private static void populateListaSemGenerics(List minhalista) {

        minhalista.add("objeto 1");

        // Aqui, estamos adicionando objetos do tipo que não gostaríamos de armazenar na lista.
        // mas aceito em tempo de compilação pois é uma instrução válida!
        minhalista.add(true);

        // Outro erro potencial, mas aceito em tempo de compilação
        minhalista.add(1000_000);

        minhalista.add("objeto 4");
        minhalista.add("objeto 5");
    }

}
