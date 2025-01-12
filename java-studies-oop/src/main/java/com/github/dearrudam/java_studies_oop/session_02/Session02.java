package com.github.dearrudam.java_studies_oop.session_02;

import net.datafaker.Faker;
import net.datafaker.providers.base.Vehicle;

import java.util.LinkedList;
import java.util.List;
import java.util.stream.IntStream;
import java.util.stream.Stream;

public class Session02 {

    // Aqui, estamos criando uma instância de Faker da biblioteca datafaker,
    // que é uma biblioteca para geração de dados falsos muito útil quando para testes.
    // No POM.xml, adicione a dependência:
    //
    // <dependency>
    //      <groupId>net.datafaker</groupId>
    //      <artifactId>datafaker</artifactId>
    //      <version>2.4.2</version>
    // </dependency>
    //
    // Sempre que possível, pegue a versão mais recente. Para mais informações,
    // acesse: https://central.sonatype.com/artifact/net.datafaker/datafaker
    //
    static Faker faker = new Faker();

    public static void main(String[] args) {

        // aqui, estamos criando uma lista de motos
        var motos = IntStream
                .range(0, 20)
                .mapToObj(i -> Veiculo.moto(faker))
                .toList();

        // aqui, estamos criando uma lista de carros
        var carros = IntStream
                .range(0, 20)
                .mapToObj(i -> Veiculo.carro(faker))
                .toList();

        // Concatenar dois streams em uma list de veículos
        var veiculos = Stream.concat(motos.stream(), carros.stream()).toList();

        // Lambdas, no Java, são objetos funcionais que podem ser passadas como
        // argumentos para métodos ou atribuídas a variáveis. Elas são usadas
        // para definir comportamentos que serão executados em um contexto específico.
        // Uma lambda Java é composta por três partes:
        //
        // 1. Uma lista de parâmetros. Se não houver parâmetros, você deve usar
        //    parênteses vazios. Quando há apenas um atributo, você pode omitir
        //    os parênteses.
        //    No java 22 é possível utilizar o carácter underscore ou underline (_) para indicar
        //    que o parâmetro não será utilizado. exemplo:
        //
        //    Consumer<Veiculo, Integer> meuBiConsumer = (veiculo, _) -> System.out.println(veiculo.marca());
        //
        //    No exemplo acima, o segundo parâmetro não será utilizado.
        //
        // 2. Uma seta (->)
        //
        // 3. Um corpo. Este pode ser uma expressão ou um bloco de código.
        //    Quando a expressão pode ser resolvida em uma única linha, você pode
        //    omitir as chaves, mas quando o corpo da lambda é mais extenso, você
        //    deve usar chaves e, se necessário, usar a palavra-chave return quando
        //    a lambda exige objeto de retorno.
        //
        //    Boas práticas recomendam poucas linhas de bloco de código. Mas quando
        //    ficar extenso, é melhor usar um método e então passar esse método como
        //    referência (Method Reference).
        //
        // A lambda seguirá o contrato da interface funcional alvo, ou seja,
        // a interface funcional que será implementada pela lambda.
        //
        // A interface funcional (Functional Interface) é qualquer interface Java
        // que contém apenas um método abstrato. Através delas é
        // possível implementar uma expressão lambda ou utilizar uma referência de método,
        // o que chamados de Method Reference.

        RegraDeNegocio<Veiculo> printarLinha = (veiculo) -> System.out.println("-".repeat(100));
        RegraDeNegocio<Veiculo> printarTipoVeiculo = (veiculo) -> System.out.printf("tipo=%s\n", veiculo.getClass().getSimpleName());
        RegraDeNegocio<Veiculo> printarMarca = (veiculo) -> System.out.printf("marca=%s\n", veiculo.marca());
        RegraDeNegocio<Veiculo> printarAno = (veiculo) -> System.out.printf("ano=%s\n", veiculo.ano());
        RegraDeNegocio<Veiculo> printarLinhaEmBranco = (veiculo) -> System.out.println("");

        // Vamos criar uma lista que irá capturar os veículos que passaram pelo filtro
        LinkedList<Veiculo> veiculosQueParticiparamDoFiltro = new LinkedList<>();

        // Agora, posso encadear as regras de negócio com os veículos que passaram pelo filtro
        var regrasCompostas = printarLinha
                .e(printarLinhaEmBranco)
                .e(printarTipoVeiculo)
                .e(printarMarca)
                .e(printarAno)
                .e(printarLinhaEmBranco)
                .e(printarLinha)
                .e(veiculosQueParticiparamDoFiltro::add);

        System.out.println("\n1o. chamada do método listarMarca...\n");

        // para que o método listarMarca possa ser usado, precisamos permitir que a
        // lista de veículos suporte objetos do tipo Veiculo ou que extende de Veículo,
        // utilizando a declaração <? extends Veiculo>;
        //
        // Para que o filtro possa ser aplicado, precisamos permitir que o filtro suporte
        // objetos do tipo Veiculo ou que seja superclasse de Veiculo, utilizando a
        // declaração <? super Veiculo>;
        //
        // Para que a regra de negócio possa ser aplicada, precisamos permitir que a regra
        // de negócio suporte objetos do tipo Veiculo ou que seja superclasse de Veiculo,
        // utilizando a declaração <? super Veiculo>;
        //
        // E com isso, podemos encadear regras de negócio com os veículos que passaram pelo filtro.
        listarMarca(veiculos,
                // o filtro que será aplicado na lista de veiculos
                // Aqui, através do Filter.and, é possível combinar filtros
                // Abaixo, estamos utilizando uma referência de método para filtrar veículos
                // Ou seja, Method Reference pode ser utilizado onde uma expressão lambda é esperada,
                // desde que ela tenha a mesma assinatura do método abstrato da interface funcional.
                Session02::filtrarPorMarcaQueContemLetraUeAnoMaiorDe2022,
                // regra de negócio que será executada com o veiculo que passou pelo filtro
                regrasCompostas);

        System.out.printf("Passaram pelo filtro %d de %d veiculos.\n",
                veiculosQueParticiparamDoFiltro.size(),
                veiculos.size());

        System.out.println("\n2o. chamada do método listarMarca...\n");
        listarMarca(veiculos,
                // o filtro que será aplicado na lista de veiculos, filtrando por marca E ano
                filtrarPorMarcaEAno(
                        // filtro que será aplicada à marca
                        marca -> marca.toLowerCase().contains("a"),
                        // filtro que será aplicado ao ano
                        ano -> ano > 2020),
                // regra de negócio que será executada com o veiculo que passou pelo filtro
                veiculo -> System.out.println(veiculo.marca()));


        // Declarações generics - super / extends
        // Utilização de Generics vaí além de declaração de tipos, também permite declaração em metodos
        // Generics permite desenvolvedores construirem estruturas flexíveis, porém seguras quanto
        //  ao tipo (o compilador irá garantir que o tipo seja o correto);
        // Favoreça métodos fabrica estáticos ao invés de construtores; Java Efetivo;
    }

    private static boolean filtrarPorMarcaQueContemLetraUeAnoMaiorDe2022(Veiculo veiculo) {
        return veiculo.marca().toLowerCase().contains("u")
                && veiculo.ano() > 2020;
    }

    private static <T extends Veiculo> Filtro<T> filtrarPorMarcaEAno(Filtro<String> marcaFiltro, Filtro<Integer> anoFiltro) {
        Filtro<T> marcaMatcher = veiculo -> marcaFiltro.teste(veiculo.marca().toLowerCase());
        Filtro<T> anoMatcher = veiculo -> anoFiltro.teste(veiculo.ano());
        return marcaMatcher.e(anoMatcher);
    }

    private static void listarMarca(
            List<? extends Veiculo> veiculos,
            Filtro<? super Veiculo> filtro,
            RegraDeNegocio<? super Veiculo> regraDeNegocio) {
        for (var veiculo : veiculos) {
            if (filtro.teste(veiculo)) {
                regraDeNegocio.execute(veiculo);
            }
        }
    }

    /**
     * Interface funcional que representa um filtro,
     * ela é correspondente a interface {@link java.util.function.Predicate}
     * <br/>
     * Lembrando que implementamos essa interface para fins didáticos,
     * pois a interface Predicate já existe no Java.
     *
     * @param <T>
     */
    @FunctionalInterface
    static interface Filtro<T> {

        boolean teste(T veiculo);

        default Filtro<T> e(Filtro<? super T> filtro) {
            return veiculo -> teste(veiculo) && filtro.teste(veiculo);
        }

        default Filtro<T> ou(Filtro<? super T> filtro) {
            return veiculo -> teste(veiculo) || filtro.teste(veiculo);
        }

        default Filtro<T> negue() {
            return veiculo -> !teste(veiculo);
        }
    }

    /**
     * Interface funcional que representa uma regra de negócio,
     * ela é correspondente a interface {@link java.util.function.Consumer}
     * <br/>
     * Lembrando que implementamos essa interface para fins didáticos,
     * pois a interface Consumer e suas variantes já existe no Java.
     *
     * @param <T> Tipo de objeto que será processado pela regra de negócio
     */
    static interface RegraDeNegocio<T> {

        void execute(T t);

        default RegraDeNegocio<T> e(RegraDeNegocio<? super T> processe) {
            return t -> {
                execute(t);
                processe.execute(t);
            };
        }
    }

    static interface Veiculo {

        public static Carro carro(String marca, int ano, String cor) {
            return new Carro(marca, ano, cor);
        }

        public static Carro carro(Faker faker) {
            Vehicle vehicle = faker.vehicle();
            return new Carro(vehicle.manufacturer(), faker.random().nextInt(2020, 2024), faker.color().name());
        }

        public static Moto moto(String marca, int ano) {
            return new Moto(marca, ano);
        }

        public static Moto moto(Faker faker) {
            Vehicle vehicle = faker.vehicle();
            return new Moto(vehicle.manufacturer(), faker.random().nextInt(2020, 2024));
        }

        public String marca();

        public int ano();
    }

    static record Moto(String marca, int ano) implements Veiculo {
    }

    static record Carro(String marca, int ano, String cor) implements Veiculo {
    }

}
