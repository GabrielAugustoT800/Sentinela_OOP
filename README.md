# SENTINELA — Sprint 1: O Domínio Base

Protótipo em **console (Java 17+)** do SENTINELA, o sistema de monitoramento de segurança física de uma
instalação nuclear **fictícia**, desenvolvido para a disciplina *Object-Oriented Programming* (Challenge
Nuclear Sentinel — AMAZUL). Todos os dados são simulados.

A Sprint 1 modela as **zonas restritas**, os **dispositivos de monitoramento** (sensores e drone de patrulha)
e a **central** que analisa as leituras e emite alertas classificados por severidade.

## Equipe e entrega

- **Turma:** 2CCP
- **Integrantes:** `Gabriel Augusto (RM: 567057)`, `Lucas Koiti (RM: 568128)`, `Lucas Morio (RM: 567616)`
- **Repositório:** `https://github.com/GabrielAugustoT800/Sentinela_OOP`
- **Disciplina:** Object-Oriented Programming — Challenge Nuclear Sentinel (AMAZUL)

## Escopo da Sprint 1

Nesta sprint, a "IA" é o motor de regras da central: `CentralMonitoramento.analisarLeituras()` percorre os
dispositivos e gera alertas classificados por severidade. A visão computacional é simulada por
`CameraVisaoComputacional`, que retorna uma cena previamente configurada via `simularCena(...)`. Não há IA real,
persistência nem interface gráfica.

## Como executar

Sem ferramentas de build (só o JDK 17+):

```bash
# compilar
mkdir -p out
javac -encoding UTF-8 -d out src/Main.java src/modelo/*.java src/servico/*.java   # Windows: use \ e listar os arquivos

# executar os 3 cenários
java -cp out Main
```

Com Maven (também roda os testes JUnit 5):

```bash
mvn test                      # testes unitários
mvn compile exec:java         # executa o Main
```

Em IDE (IntelliJ/Eclipse/VS Code): abra a pasta, marque `src` como *Sources*, `test` como *Tests*, execute `Main`
e use a aba **Testing** para rodar os testes (JUnit 5). Se não tiver Maven instalado, a IDE executa os testes
diretamente.

### Testes sem Maven (JUnit Platform Console)

Se você não tiver Maven, rode os testes com o `junit-platform-console-standalone` (Java 17+). Baixe o jar
uma vez:

```powershell
Invoke-WebRequest -UseBasicParsing -Uri "https://repo1.maven.org/maven2/org/junit/platform/junit-platform-console-standalone/1.10.2/junit-platform-console-standalone-1.10.2.jar" -OutFile "junit-platform-console-standalone-1.10.2.jar"
```

Compile o código principal e os testes:

```powershell
$jar = "junit-platform-console-standalone-1.10.2.jar"

javac -encoding UTF-8 -d out src\Main.java src\modelo\*.java src\servico\*.java

$tests = (Get-ChildItem test -Recurse -Filter *.java | ForEach-Object { $_.FullName })
javac -encoding UTF-8 -cp "out;$jar" -d out-test $tests
```

Execute todos os testes ou filtre por pacote/classe:

```powershell
$jar = "junit-platform-console-standalone-1.10.2.jar"

# todos os 40 testes
java -jar $jar execute --class-path "out;out-test" --scan-classpath out-test --details=tree --disable-banner

# somente o pacote modelo (37 testes)
java -jar $jar execute --class-path "out;out-test" --select-package modelo --details=summary --disable-banner

# somente o pacote servico (3 testes)
java -jar $jar execute --class-path "out;out-test" --select-package servico --details=summary --disable-banner

# uma classe específica
java -jar $jar execute --class-path "out;out-test" --select-class modelo.ZonaRestritaTest --details=tree --disable-banner
```

> É necessário ter `java` e `javac` (JDK 17+) no `PATH`. No VS Code, o JDK baixado pela extensão Java fica em
> `%USERPROFILE%\.vscode\extensions\redhat.java-*\jre\*\bin`. Em Linux/macOS, troque o separador `;` do
> `--class-path` por `:`.

## Estrutura

```text
sentinela/
├── src/
│   ├── Main.java                         # executa os 3 cenários, na ordem
│   ├── modelo/
│   │   ├── ZonaRestrita.java
│   │   ├── DispositivoMonitoramento.java # superclasse (abstract)
│   │   ├── SensorRadiacao.java
│   │   ├── SensorPerimetral.java
│   │   ├── DronePatrulha.java
│   │   ├── AlertaSeguranca.java
│   │   └── CentralMonitoramento.java
│   └── servico/
│       ├── CameraVisaoComputacional.java # visão computacional SIMULADA
│       └── ResultadoVisao.java
└── test/                                 # JUnit 5 (40 testes)
```

## Modelo

```text
CentralMonitoramento ── TEM VÁRIOS ──> DispositivoMonitoramento (abstract)
                                          ├── É UM ── SensorRadiacao ── TEM UMA ──> ZonaRestrita
                                          ├── É UM ── SensorPerimetral ─ TEM UMA ──> ZonaRestrita
                                          └── É UM ── DronePatrulha ──── TEM UMA ──> CameraVisaoComputacional

AlertaSeguranca ── TEM UM ──> DispositivoMonitoramento (sensorOrigem)
                └─ TEM UMA ─> ZonaRestrita (zonaAfetada)
```

A classe `Sensor` prevista na etapa de associação corresponde, na versão final, a `DispositivoMonitoramento`.
Atributos principais de cada classe:

| Classe | Atributos principais | Responsabilidade |
|---|---|---|
| `ZonaRestrita` | `identificador`, `descricao`, `limiteRadiacao`, `nivelRadiacaoAtual` | Validar leituras e classificar o risco |
| `DispositivoMonitoramento` | `codigoIdentificador`, `localizacao`, `emOperacao` | Base comum dos dispositivos |
| `SensorRadiacao` | `zonaMonitorada`, `limiteSeguranca`, `leituraAtual` | Registrar a leitura na zona monitorada |
| `SensorPerimetral` | `zonaVigiada`, `setorVigiado`, `violacaoDetectada` | Detectar violação da cerca |
| `DronePatrulha` | `rotaPatrulha`, `bateriaPercentual`, `camera`, `destinoAtual` | Patrulhar e confirmar ocorrências |
| `AlertaSeguranca` | `sensorOrigem`, `zonaAfetada`, `severidade`, `mensagem` | Representar um alerta imutável |
| `CentralMonitoramento` | `dispositivos` | Analisar leituras e gerar alertas |

## Saída do console

Saída real obtida com `java -cp out Main` (a exibição dos acentos depende do encoding do terminal):

```text
Cenário 1 — Rotina normal
Zona ZNR-01 - Casa do Reator
Leitura registrada: 0,8 µSv/h
Classificação: NORMAL

Zona ZNR-02 - Pátio de Armazenamento de Combustível Irradiado
Leitura registrada: 0,5 µSv/h
Classificação: NORMAL

Perímetro íntegro.
Drone em patrulha. Nenhuma detecção.
Nenhum alerta gerado.

Cenário 2 — Pico de radiação
Zona ZNR-01 - Casa do Reator
Leitura registrada: 4,7 µSv/h
Classificação: CRITICO

ALERTA CRÍTICO
Origem: RAD-01
Localização: Casa do Reator
Mensagem: Pico de radiação detectado na Casa do Reator.

Cenário 3 — Acesso indevido
Sensor perimetral PER-01 detectou violação no Setor Norte.
ALERTA DE ATENÇÃO
Origem: PER-01
Localização: Perímetro Norte
Mensagem: Violação perimetral detectada no Setor Norte. Aguardando confirmação.
Drone DRN-01 enviado para patrulha.
Câmera de visão computacional simulada analisou a imagem.
Resultado: VEICULO_NAO_IDENTIFICADO

ALERTA CRÍTICO
Origem: PER-01
Localização: Perímetro Norte
Mensagem: Violação perimetral confirmada por drone. Veículo não identificado detectado.
```

## Regras de negócio

| Faixa de radiação (limite = L) | Classificação |
|---|---|
| até L | `NORMAL` |
| acima de L, até 2·L | `ATENCAO` |
| acima de 2·L | `CRITICO` |

* Leitura negativa, `NaN` ou acima de **1.000.000 µSv/h** (falha de sensor) é rejeitada com
  `IllegalArgumentException`, e o último valor válido é mantido.
* Violação perimetral isolada gera alerta de **ATENÇÃO**; confirmada pelo drone (veículo não identificado), é
  escalonada para **CRÍTICO**.
* Dispositivos fora de operação são ignorados pela central.

## Decisões de projeto

* **Regra dos 3 parâmetros:** os construtores de `ZonaRestrita`, `DispositivoMonitoramento`, `SensorRadiacao`,
  `SensorPerimetral` e `DronePatrulha` têm no máximo 3 parâmetros. Para isso, o dispositivo nasce *em operação*
  (há `colocarForaDeOperacao()`), e a localização dos sensores vem da zona que eles vigiam. A única exceção é
  `AlertaSeguranca` (4 parâmetros): é um objeto-valor imutável, só carrega dados e não tem regra de negócio.
* **Nada de zona genérica na central:** cada sensor já conhece a `ZonaRestrita` associada; o alerta recebe essa
  mesma zona (a central não cria zonas "de mentirinha").
* **Uma única cópia da validação de leitura:** `SensorRadiacao.registrarLeitura` delega à zona; só guarda a
  leitura depois que a zona a aceitou.
* **Câmera simulada configurável:** `simularCena(...)` define o que ela "enxerga", assim o cenário 1 (sem
  detecção) e o cenário 3 (veículo) usam a mesma classe de forma determinística.
* **Cenários independentes:** cada cenário monta a sua própria instalação fictícia (classe interna `Instalacao`).
  As duas zonas obrigatórias (`ZNR-01` e `ZNR-02`) são criadas ali; `ZNR-03` é uma zona adicional usada pelo
  sensor perimetral.
* A central guarda os dispositivos em uma `List` (`ArrayList`), em vez de array fixo, para poder adicionar
  dispositivos livremente. O enunciado cita "array"; a coleção foi escolhida por ser mais flexível, e
  `analisarLeituras()` percorre todos os dispositivos e gera um alerta para cada situação atípica.
* **Clean Code:** classes são nomeadas por substantivos (`ZonaRestrita`, `CentralMonitoramento`) e métodos por
  verbos no infinitivo (`registrarLeituraRadiacao`, `classificarRisco`, `analisarLeituras`), sem abreviações.

## Perguntas de reflexão

**1. Classes e objetos — Por que `ZonaRestrita` é uma classe e "ZNR-01 - Casa do Reator" é um objeto?**
A classe é o molde: define quais dados toda zona restrita tem (identificador, descrição, limite, nível atual) e o
que ela sabe fazer (registrar leitura, classificar risco). "ZNR-01 - Casa do Reator" é uma instância concreta
desse molde, com valores próprios e estado próprio. Existe uma classe, mas podem existir muitos objetos
(`ZNR-01`, `ZNR-02`...), cada um com seu nível de radiação.

**2. Métodos e comportamentos — Por que a regra que classifica o risco deve morar dentro de `ZonaRestrita`, e não espalhada em `if`s do `main`?**
Porque a regra depende só do estado da própria zona (limite e nível atual). Dentro da classe, existe uma única
versão da regra, testável isoladamente. Espalhada pelo `main`, qualquer mudança de faixa exigiria caçar vários
pontos do código, e um deles esquecido geraria classificações inconsistentes para a mesma leitura.

**3. Encapsulamento — Se `nivelRadiacaoAtual` fosse público, que tipo de fraude seria possível?**
Qualquer código poderia fazer `zona.nivelRadiacaoAtual = -5` (valor fisicamente impossível) ou `= 0` para
*esconder* um pico real e manter o relatório "NORMAL"; também poderia injetar um valor alto para forjar uma
emergência. Sem passar por `registrarLeituraRadiacao`, nenhuma validação roda e o relatório da central deixa de
ser confiável.

**4. Construtores — Por que o desaparecimento do construtor padrão protege o cadastro de zonas?**
Em Java, ao declarar qualquer construtor, o compilador deixa de gerar o construtor sem parâmetros. Assim,
`new ZonaRestrita()` **não compila** e é impossível criar uma "zona fantasma" sem identificador, descrição ou
limite. Todo objeto nasce válido: a regra é garantida pelo compilador, não pela boa vontade de quem programa.

**5. Associação (TEM-UM) — Se o alerta guardasse só a `String` do código do sensor, o que a central perderia?**
Perderia o acesso aos próprios objetos: para escalonar o incidente teria que procurar o sensor por código em
alguma lista, sem garantia de achá-lo, e não teria a zona afetada à mão (descrição, limite, nível atual). Com
`sensorOrigem` e `zonaAfetada` como referências, localização, setor vigiado, estado de operação e radiação atual
estão a um `get` de distância, e é o *mesmo* objeto que disparou o evento (passagem por referência).

**6. Herança (É-UM) — Se a mãe tem `codigoIdentificador` privado, por que a filha precisa passá-lo via `super()`? Que princípio é protegido?**
Porque `private` impede a filha de acessar o atributo da mãe (`this.codigoIdentificador = ...` nem compila).
O `super(...)` chama o construtor da mãe, que valida e atribui o dado uma única vez. O princípio protegido é o
**encapsulamento**: o estado da classe só é alterado pelos caminhos que ela mesma controla, e as regras de
validação ficam num só lugar, sem duplicação nas subclasses.

## Testes unitários (JUnit 5)

São **40 testes**, distribuídos em:

| Classe de teste | Testes |
|---|---:|
| `ZonaRestritaTest` | 13 |
| `CentralMonitoramentoTest` | 11 |
| `DispositivoMonitoramentoTest` | 10 |
| `AlertaSegurancaTest` | 3 |
| `CameraVisaoComputacionalTest` | 3 |

Cobrem todas as sugestões do enunciado:

| Sugestão do enunciado | Teste que cobre |
|---|---|
| Zona não nula após instanciação | `ZonaRestritaTest.deveCriarZonaRestritaValida` |
| 0,2 → 4,7 µSv/h muda `NORMAL` → `CRITICO` | `ZonaRestritaTest.deveAtualizarRadiacaoEClassificarComoCritico` |
| Leitura -5 rejeitada e último valor mantido | `ZonaRestritaTest.deveRejeitarLeituraNegativaEManterUltimoValorValido` |
| Identificador vazio rejeitado | `ZonaRestritaTest.deveRejeitarZonaComIdentificadorVazio` |
| `getSensorOrigem()` devolve o mesmo objeto | `AlertaSegurancaTest.deveManterReferenciaDoSensorOrigemNoAlerta` |
| As três filhas herdam `getCodigoIdentificador()` | `DispositivoMonitoramentoTest.deveHerdarCodigoIdentificadorDaClasseMae` |

Além disso: fronteiras das faixas, leitura absurda/`NaN`, alertas da central (radiação, perímetro, drone +
câmera), dispositivos fora de operação e validações do drone.