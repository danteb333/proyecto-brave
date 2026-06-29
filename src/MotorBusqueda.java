import java.util.HashMap;

public class MotorBusqueda {

    private final HashMap<String,String> datosPrecargados;

    public MotorBusqueda() {
        datosPrecargados=new HashMap<>();
        precargardatos();
    }

    public void precargardatos(){
        String htmlUniversidades = "<html><body style='font-family: Arial; padding: 20px;'>" +
                "<h2>Resultados para: Universidades de Chile</h2>" +
                "<ol>" +
                "<li><a href='https://www.uchile.cl'>Universidad de Chile</a></li>" +
                "<li><a href='https://www.puc.cl'>Pontificia Universidad Católica de Chile</a></li>" +
                "<li><a href='https://www.utalca.cl'>Universidad de Talca</a></li>" +
                "<li><a href='https://www.udec.cl'>Universidad de Concepción</a></li>" +
                "<li><a href='https://www.usach.cl'>Universidad de Santiago de Chile</a></li>" +
                "<li><a href='https://www.usm.cl'>Universidad Técnica Federico Santa María</a></li>" +
                "<li><a href='https://www.pucv.cl'>Pontificia Universidad Católica de Valparaíso</a></li>" +
                "<li><a href='https://www.unab.cl'>Universidad Andrés Bello</a></li>" +
                "<li><a href='https://www.udp.cl'>Universidad Diego Portales</a></li>" +
                "<li><a href='https://www.uv.cl'>Universidad de Valparaíso</a></li>" +
                "</ol></body></html>";

        datosPrecargados.put("universidades de chile", htmlUniversidades);


        String htmlFutbol = "<html><body style='font-family: Arial; padding: 20px;'>" +
                "<h2>Resultados para: Equipos de fútbol chileno</h2>" +
                "<ul>" +
                "<li><a href='https://www.colocolo.cl'>1. Colo-Colo</a></li>" +
                "<li><a href='https://www.udechile.cl'>2. Universidad de Chile</a></li>" +
                "<li><a href='https://www.cruzados.cl'>3. Universidad Católica</a></li>" +
                "<li><a href='https://www.cobreloa.cl'>4. Cobreloa</a></li>" +
                "<li><a href='https://www.unionespanola.cl'>5. Unión Española</a></li>" +
                "<li><a href='https://www.everton.cl'>6. Everton de Viña del Mar</a></li>" +
                "<li><a href='https://www.santiagowanderers.cl'>7. Santiago Wanderers</a></li>" +
                "<li><a href='https://www.ohigginsfc.cl'>8. O'Higgins de Rancagua</a></li>" +
                "<li><a href='https://www.huachipatofc.cl'>9. Huachipato</a></li>" +
                "<li><a href='https://www.coquimbounido.cl'>10. Coquimbo Unido</a></li>" +
                "</ul></body></html>";

        datosPrecargados.put("equipos de futbol chileno", htmlFutbol);


        String htmlCarreras = "<html><body style='font-family: Arial; padding: 20px;'>" +
                "<h2>Resultados para: Carreras universitarias en Chile</h2>" +
                "<ul>" +
                "<li><a href='https://www.mifuturo.cl/carreras/medicina'>1. Medicina</a></li>" +
                "<li><a href='https://www.mifuturo.cl/carreras/derecho'>2. Derecho</a></li>" +
                "<li><a href='https://www.mifuturo.cl/carreras/ingenieria-civil-informatica'>3. Ingeniería Civil en Computación / Informática</a></li>" +
                "<li><a href='https://www.mifuturo.cl/carreras/enfermeria'>4. Enfermería</a></li>" +
                "<li><a href='https://www.mifuturo.cl/carreras/psicologia'>5. Psicología</a></li>" +
                "<li><a href='https://www.mifuturo.cl/carreras/ingenieria-comercial'>6. Ingeniería Comercial</a></li>" +
                "<li><a href='https://www.mifuturo.cl/carreras/arquitectura'>7. Arquitectura</a></li>" +
                "<li><a href='https://www.mifuturo.cl/carreras/kinesiologia'>8. Kinesiología</a></li>" +
                "<li><a href='https://www.mifuturo.cl/carreras/medicina-veterinaria'>9. Medicina Veterinaria</a></li>" +
                "<li><a href='https://www.mifuturo.cl/carreras/auditoria'>10. Contador Auditor</a></li>" +
                "</ul></body></html>";

        datosPrecargados.put("carreras universitarias de chile", htmlCarreras);


        String htmlAutos = "<html><body style='font-family: Arial; padding: 20px;'>" +
                "<h2>Resultados para: Autos más confiables del mercado</h2>" +
                "<ol>" +
                "<li><a href='https://www.toyota.cl/modelos/corolla'>Toyota Corolla</a></li>" +
                "<li><a href='https://www.honda.cl/modelos/civic'>Honda Civic</a></li>" +
                "<li><a href='https://www.mazda.cl/modelos/mazda3'>Mazda 3</a></li>" +
                "<li><a href='https://www.toyota.cl/modelos/rav4'>Toyota RAV4</a></li>" +
                "<li><a href='https://www.honda.cl/modelos/crv'>Honda CR-V</a></li>" +
                "<li><a href='https://www.subaru.cl/modelos/forester'>Subaru Forester</a></li>" +
                "<li><a href='https://www.suzuki.cl/modelos/swift'>Suzuki Swift</a></li>" +
                "<li><a href='https://www.mazda.cl/modelos/cx5'>Mazda CX-5</a></li>" +
                "<li><a href='https://www.hyundai.cl/modelos/accent'>Hyundai Accent</a></li>" +
                "<li><a href='https://www.kia.cl/modelos/rio'>Kia Rio</a></li>" +
                "</ol></body></html>";

        datosPrecargados.put("autos mas confiables", htmlAutos);

        String htmlCamionetas = "<html><body style='font-family: Arial; padding: 20px;'>" +
                "<h2>Resultados para: Camionetas para trabajo pesado</h2>" +
                "<ol>" +
                "<li><a href='https://www.ssangyong.cl/modelos/actyon-sports'>SsangYong Actyon Sports</a></li>" +
                "<li><a href='https://www.toyota.cl/modelos/hilux'>Toyota Hilux</a></li>" +
                "<li><a href='https://www.mitsubishi-motors.cl/modelos/l200'>Mitsubishi L200</a></li>" +
                "<li><a href='https://www.nissan.cl/vehiculos/nuevos/navara.html'>Nissan Navara</a></li>" +
                "<li><a href='https://www.mahindra.cl/modelos/pik-up'>Mahindra Pik-Up</a></li>" +
                "<li><a href='https://www.ford.cl/camionetas/ranger'>Ford Ranger</a></li>" +
                "<li><a href='https://www.chevrolet.cl/camionetas/colorado'>Chevrolet Colorado</a></li>" +
                "<li><a href='https://www.maxus.cl/t60'>Maxus T60</a></li>" +
                "<li><a href='https://www.greatwall.cl/poer'>Great Wall Poer</a></li>" +
                "<li><a href='https://www.foton.cl/terracota'>Foton Terracota</a></li>" +
                "</ol></body></html>";

        datosPrecargados.put("camionetas para trabajo pesado", htmlCamionetas);

        String htmlIDEs = "<html><body style='font-family: Arial; padding: 20px;'>" +
                "<h2>Resultados para: Entornos de desarrollo para Java</h2>" +
                "<ul>" +
                "<li><a href='https://www.jetbrains.com/idea/'>1. IntelliJ IDEA</a></li>" +
                "<li><a href='https://www.eclipse.org/'>2. Eclipse IDE</a></li>" +
                "<li><a href='https://netbeans.apache.org/'>3. Apache NetBeans</a></li>" +
                "<li><a href='https://code.visualstudio.com/'>4. Visual Studio Code</a></li>" +
                "<li><a href='https://developer.android.com/studio'>5. Android Studio</a></li>" +
                "<li><a href='https://bluej.org/'>6. BlueJ</a></li>" +
                "<li><a href='https://www.greenfoot.org/'>7. Greenfoot</a></li>" +
                "<li><a href='https://www.oracle.com/tools/jdeveloper/'>8. Oracle JDeveloper</a></li>" +
                "<li><a href='http://drjava.org/'>9. DrJava</a></li>" +
                "<li><a href='http://www.jcreator.com/'>10. JCreator</a></li>" +
                "</ul></body></html>";

        datosPrecargados.put("entornos de desarrollo para java", htmlIDEs);

        String htmlSQL = "<html><body style='font-family: Arial; padding: 20px;'>" +
                "<h2>Resultados para: Bases de datos SQL más usadas</h2>" +
                "<ol>" +
                "<li><a href='https://www.mysql.com/'>MySQL</a></li>" +
                "<li><a href='https://www.postgresql.org/'>PostgreSQL</a></li>" +
                "<li><a href='https://www.oracle.com/database/'>Oracle Database</a></li>" +
                "<li><a href='https://www.microsoft.com/sql-server/'>Microsoft SQL Server</a></li>" +
                "<li><a href='https://mariadb.org/'>MariaDB</a></li>" +
                "<li><a href='https://sqlite.org/'>SQLite</a></li>" +
                "<li><a href='https://www.ibm.com/analytics/db2'>IBM Db2</a></li>" +
                "<li><a href='https://aws.amazon.com/rds/aurora/'>Amazon Aurora</a></li>" +
                "<li><a href='https://www.sap.com/products/hana.html'>SAP HANA</a></li>" +
                "<li><a href='https://www.teradata.com/'>Teradata</a></li>" +
                "</ol></body></html>";

        datosPrecargados.put("bases de datos sql mas usadas", htmlSQL);

        String htmlElectronica = "<html><body style='font-family: Arial; padding: 20px;'>" +
                "<h2>Resultados para: Herramientas para reparación de electrónica</h2>" +
                "<ol>" +
                "<li><a href='https://tiendaelectronica.cl/cautin-regulable'>Cautín de temperatura regulable</a></li>" +
                "<li><a href='https://tiendaelectronica.cl/multimetro-digital'>Multímetro digital</a></li>" +
                "<li><a href='https://tiendaelectronica.cl/estacion-calor'>Estación de soldadura y aire caliente</a></li>" +
                "<li><a href='https://tiendaelectronica.cl/flux-soldar'>Flux para soldar (Líquido o en pasta)</a></li>" +
                "<li><a href='https://tiendaelectronica.cl/malla-desoldadora'>Malla desoldadora de cobre</a></li>" +
                "<li><a href='https://tiendaelectronica.cl/estano-60-40'>Rollo de estaño 60/40</a></li>" +
                "<li><a href='https://tiendaelectronica.cl/pinzas-antiestaticas'>Set de pinzas de precisión antiestáticas</a></li>" +
                "<li><a href='https://tiendaelectronica.cl/pulsera-antiestatica'>Pulsera antiestática</a></li>" +
                "<li><a href='https://tiendaelectronica.cl/lupa-led'>Lupa con base y luz LED</a></li>" +
                "<li><a href='https://tiendaelectronica.cl/fuente-poder'>Fuente de poder regulable de laboratorio</a></li>" +
                "</ol></body></html>";

        datosPrecargados.put("herramientas para reparacion de electronica", htmlElectronica);

        String htmlParques = "<html><body style='font-family: Arial; padding: 20px;'>" +
                "<h2>Resultados para: Parques Nacionales de Chile</h2>" +
                "<ul>" +
                "<li><a href='https://www.conaf.cl/parque-nacional-torres-del-paine/'>1. Parque Nacional Torres del Paine</a></li>" +
                "<li><a href='https://www.conaf.cl/parque-nacional-conguillio/'>2. Parque Nacional Conguillío</a></li>" +
                "<li><a href='https://www.conaf.cl/parque-nacional-vicente-perez-rosales/'>3. Parque Nacional Vicente Pérez Rosales</a></li>" +
                "<li><a href='https://www.conaf.cl/parque-nacional-queulat/'>4. Parque Nacional Queulat</a></li>" +
                "<li><a href='https://www.conaf.cl/parque-nacional-villarrica/'>5. Parque Nacional Villarrica</a></li>" +
                "<li><a href='https://www.conaf.cl/parque-nacional-radal-siete-tazas/'>6. Parque Nacional Radal Siete Tazas</a></li>" +
                "<li><a href='https://www.conaf.cl/parque-nacional-laguna-san-rafael/'>7. Parque Nacional Laguna San Rafael</a></li>" +
                "<li><a href='https://www.conaf.cl/parque-nacional-puyehue/'>8. Parque Nacional Puyehue</a></li>" +
                "<li><a href='https://www.conaf.cl/parque-nacional-pan-de-azucar/'>9. Parque Nacional Pan de Azúcar</a></li>" +
                "<li><a href='https://www.conaf.cl/parque-nacional-alerce-andino/'>10. Parque Nacional Alerce Andino</a></li>" +
                "</ul></body></html>";

        datosPrecargados.put("parques nacionales de chile", htmlParques);

        String htmlPatrones = "<html><body style='font-family: Arial; padding: 20px;'>" +
                "<h2>Resultados para: Patrones de diseño de software (GoF)</h2>" +
                "<ol>" +
                "<li><a href='https://refactoring.guru/es/design-patterns/singleton'>Singleton (Creacional)</a></li>" +
                "<li><a href='https://refactoring.guru/es/design-patterns/factory-method'>Factory Method (Creacional)</a></li>" +
                "<li><a href='https://refactoring.guru/es/design-patterns/builder'>Builder (Creacional)</a></li>" +
                "<li><a href='https://refactoring.guru/es/design-patterns/observer'>Observer (Comportamiento)</a></li>" +
                "<li><a href='https://refactoring.guru/es/design-patterns/strategy'>Strategy (Comportamiento)</a></li>" +
                "<li><a href='https://refactoring.guru/es/design-patterns/command'>Command (Comportamiento)</a></li>" +
                "<li><a href='https://refactoring.guru/es/design-patterns/decorator'>Decorator (Estructural)</a></li>" +
                "<li><a href='https://refactoring.guru/es/design-patterns/adapter'>Adapter (Estructural)</a></li>" +
                "<li><a href='https://refactoring.guru/es/design-patterns/facade'>Facade (Estructural)</a></li>" +
                "<li><a href='https://es.wikipedia.org/wiki/Modelo%E2%80%93vista%E2%80%93controlador'>MVC (Modelo-Vista-Controlador)</a></li>" +
                "</ol></body></html>";

        datosPrecargados.put("patrones de diseño de software", htmlPatrones);
    }

    public String buscarDatos(String busqueda){
        String consulta=busqueda.trim().toLowerCase();

        if (datosPrecargados.containsKey(consulta))
            return datosPrecargados.get(consulta);
        else
            return "<html><body style='font-family: Arial; padding: 20px;'>" +
                    "<h3>No se encontraron resultados para: <i>" + consulta + "</i></h3>" +
                    "<p>Asegúrate de escribir la pregunta exacta.</p>" +
                    "</body></html>";
    }
}



