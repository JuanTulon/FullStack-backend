package com.joyeria.joyeria;

import com.joyeria.joyeria.model.*;
import com.joyeria.joyeria.repository.*;
import com.joyeria.joyeria.util.RutUtils;
import net.datafaker.Faker;
import org.springframework.boot.CommandLineRunner;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.util.*;
import java.util.concurrent.TimeUnit;

@Component
public class DataLoader implements CommandLineRunner {

    private final UsuarioRepository usuarioRepository;
    private final RolUsuarioRepository rolUsuarioRepository;
    private final CategoriaRepository categoriaRepository;
    private final ProductoRepository productoRepository;
    private final PedidoRepository pedidoRepository;
    private final DetallePedidoRepository detallePedidoRepository; // Ojo con el nombre "Detall"
    private final EnvioRepository envioRepository;
    private final PasswordEncoder passwordEncoder;
    
    private final Faker faker = new Faker(new Locale("es")); // Datos en español

    public DataLoader(UsuarioRepository usuarioRepository,
                      RolUsuarioRepository rolUsuarioRepository,
                      CategoriaRepository categoriaRepository,
                      ProductoRepository productoRepository,
                      PedidoRepository pedidoRepository,
                      DetallePedidoRepository detallePedidoRepository,
                      EnvioRepository envioRepository,
                      PasswordEncoder passwordEncoder) {
        this.usuarioRepository = usuarioRepository;
        this.rolUsuarioRepository = rolUsuarioRepository;
        this.categoriaRepository = categoriaRepository;
        this.productoRepository = productoRepository;
        this.pedidoRepository = pedidoRepository;
        this.detallePedidoRepository = detallePedidoRepository;
        this.envioRepository = envioRepository;
        this.passwordEncoder = passwordEncoder;
    }

    @Override
    @Transactional
    public void run(String... args) throws Exception {
        // Solo cargar datos si la base de datos está vacía (chequeamos usuarios)
        if (usuarioRepository.count() == 0) {
            System.out.println("--- INICIANDO CARGA DE DATOS DE PRUEBA (DATAFAKER) ---");
            
            crearRoles();
            List<Usuario> usuarios = crearUsuarios(5); // Crea 5 usuarios
            Map<String, Categoria> categorias = crearCategorias();
            List<Producto> productos = cargarProductosCatalogo(categorias);
            crearPedidos(5, usuarios, productos);

            System.out.println("--- CARGA DE DATOS COMPLETADA CON ÉXITO ---");
        }
    }

    private void crearRoles() {
        if (rolUsuarioRepository.count() == 0) {
            // CAMBIO: Pasamos 'null' como ID para que Hibernate sepa que es NUEVO y haga INSERT
            rolUsuarioRepository.save(new RolUsuario(null, RolUsuario.NombreRol.ROLE_ADMIN));
            rolUsuarioRepository.save(new RolUsuario(null, RolUsuario.NombreRol.ROLE_EMPLEADO));
            rolUsuarioRepository.save(new RolUsuario(null, RolUsuario.NombreRol.ROLE_USUARIO));
        }
    }

    private List<Usuario> crearUsuarios(int cantidad) {
        List<Usuario> usuarios = new ArrayList<>();
        // Obtener el rol de usuario por defecto
        RolUsuario rolUser = rolUsuarioRepository.findByNombreRol(RolUsuario.NombreRol.ROLE_USUARIO).orElseThrow();
        Set<RolUsuario> roles = new HashSet<>();
        roles.add(rolUser);

        // Crear un ADMIN fijo para que puedas probar
        Usuario admin = new Usuario();
        admin.setRun("12345678");
        admin.setDv("5");
        admin.setNombre("Admin");
        admin.setApellido1("Sistema");
        admin.setApellido2("Joyeria");
        admin.setEmail("admin@joyeria.com");
        admin.setPassword(passwordEncoder.encode("Admin123."));
        admin.setTelefono(999999999);
        admin.setFechaNacimiento(new Date());
        
        // Asignar rol ADMIN
        RolUsuario rolAdmin = rolUsuarioRepository.findByNombreRol(RolUsuario.NombreRol.ROLE_ADMIN).orElseThrow();
        admin.setRoles(Collections.singleton(rolAdmin)); // Set inmutable, cuidado si modificas después
        usuarioRepository.save(admin);

        // Crear usuarios aleatorios
        for (int i = 0; i < cantidad; i++) {
            Usuario u = new Usuario();
            String nombre = faker.name().firstName();
            String ape1 = faker.name().lastName();
            String ape2 = faker.name().lastName();
            
            // Generar RUT válido aleatorio (simple)
            int run = faker.number().numberBetween(10000000, 25000000);
            String dv = RutUtils.calcularDV(run);
            String rutCompleto = run + "-" + dv;
            if (!RutUtils.validarRut(rutCompleto)) {
                continue; // Intenta de nuevo si el RUT no es válido
            }
            
            u.setRun(String.valueOf(run));
            u.setDv(dv);
            u.setNombre(nombre);
            u.setApellido1(ape1);
            u.setApellido2(ape2);
            u.setEmail(faker.internet().emailAddress(nombre.toLowerCase() + "." + ape1.toLowerCase()));
            u.setTelefono(faker.number().numberBetween(900000000, 999999999));
            u.setPassword(passwordEncoder.encode("123456")); // Todos con clave 123456
            u.setFechaNacimiento(faker.date().birthday(18, 80));
            u.setRoles(roles);

            usuarios.add(usuarioRepository.save(u));
        }
        return usuarios;
    }

    private Map<String, Categoria> crearCategorias() {
        Map<String, Categoria> mapa = new HashMap<>();
        // Nombres exactos que usabas en el JSON (en minúsculas para coincidir con las claves)
        String[] nombres = {"collares", "anillos", "aros", "pulseras", "relojes"};
        
        for (String nombre : nombres) {
            Categoria c = new Categoria();
            // Guardamos con mayúscula inicial para que se vea bonito (ej: "Collares")
            c.setNombreCategoria(nombre.substring(0, 1).toUpperCase() + nombre.substring(1));
            mapa.put(nombre, categoriaRepository.save(c));
        }
        return mapa;
    }

    private List<Producto> cargarProductosCatalogo(Map<String, Categoria> cats) {
        List<Producto> lista = new ArrayList<>();

        // Aquí están tus productos "fijos" del JSON
        
        lista.add(crearProducto("Collar de oro", "Collar fino con detalles de estrellas y lunas", 
            350000, 15, "/images/imagen_para_2.jpg", cats.get("collares")));

        lista.add(crearProducto("Cadena de Eslabones", "Cadena de eslabones rectangulares", 
            400000, 8, "/images/imagen_pi_2.jpg", cats.get("collares")));

        lista.add(crearProducto("Anillo de Oro", "Anillo clásico en oro de 18k", 
            200000, 12, "/images/imagen_pi_3.jpg", cats.get("anillos")));

        lista.add(crearProducto("Aretes de Colgantes", "Aretes elegantes con diseño de colgante", 
            150000, 20, "/images/imagen_pi_5.jpg", cats.get("aros")));

        lista.add(crearProducto("Set de aros", "Set de aros de oro de 9k", 
            50000, 10, "/images/imagen_pi_6.jpg", cats.get("aros")));

        lista.add(crearProducto("Cadena con dije", "Cadena de oro con dije en forma de chapa", 
            60000, 5, "/images/Imagen_tendencia_1.jpg", cats.get("collares")));

        lista.add(crearProducto("Set de 2 aros", "Set de 2 aros bañados en oro", 
            45000, 18, "/images/Imagen_tendencia_2.jpg", cats.get("aros")));

        lista.add(crearProducto("Anillo de oro con piedra", "Anillo de oro con piedra central redonda", 
            250000, 7, "/images/Imagen_tendencia_3.jpg", cats.get("anillos")));

        lista.add(crearProducto("Brazalete", "Brazalete de oro liso", 
            90000, 10, "/images/Imagen_tendencia_4.jpg", cats.get("pulseras")));

        lista.add(crearProducto("Pulsera de eslabones", "Pulsera de eslabones gruesos", 
            120000, 14, "/images/pulseraH.jpg", cats.get("pulseras")));

        lista.add(crearProducto("Reloj", "Reloj de acero inoxidable con movimiento de cuarzo", 
            50000, 25, "/images/reloj1.jpg", cats.get("relojes")));

        lista.add(crearProducto("Reloj Invicta pro diver", "Reloj invicta plateado con detalles en dorado", 
            214000, 30, "/images/reloj2.jpg", cats.get("relojes")));

        return lista;
    }

    private Producto crearProducto(String nombre, String desc, Integer precio, Integer stock, String foto, Categoria cat) {
        Producto p = new Producto();
        p.setNombreProducto(nombre);
        p.setDescripcionProducto(desc);
        p.setPrecio(precio);
        p.setStock(stock);
        p.setFoto(foto);
        p.setCategoria(cat);
        
        return productoRepository.save(p);
    }

    private void crearPedidos(int cantidad, List<Usuario> usuarios, List<Producto> productos) {
        // Generamos algunos pedidos de prueba usando los productos reales
        for (int i = 0; i < cantidad; i++) {
            try {
                Pedido pedido = new Pedido();
                Usuario cliente = usuarios.get(faker.random().nextInt(usuarios.size()));
                
                pedido.setUsuario(cliente);
                pedido.setFechaPedido(faker.date().past(30, TimeUnit.DAYS));
                pedido.setDireccionEnvio(faker.address().fullAddress());
                pedido.setMetodoPago("WebPay");
                pedido.setEstadoPedido("Enviado");
                pedido.setTotalPedido(0); 
                
                pedido = pedidoRepository.save(pedido);

                DetallePedido det = new DetallePedido();
                Producto prod = productos.get(faker.random().nextInt(productos.size()));
                
                det.setPedido(pedido);
                det.setProducto(prod);
                det.setCantidadProducto(1);
                det.setSubtotal(prod.getPrecio());
                
                detallePedidoRepository.save(det);
                
                pedido.setTotalPedido(det.getSubtotal());
                pedidoRepository.save(pedido);
                
                // Crear envío
                Envio envio = new Envio();
                envio.setPedido(pedido);
                envio.setFecha_envio(new Date());
                envio.setEstado_envio("En Camino");
                envioRepository.save(envio);

            } catch (Exception e) {
                // Ignorar errores de generación aleatoria
            }
        }
    }
}