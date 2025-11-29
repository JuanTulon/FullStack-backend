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
            List<Usuario> usuarios = crearUsuarios(10); // Crea 10 usuarios
            List<Categoria> categorias = crearCategorias();
            List<Producto> productos = crearProductos(20, categorias); // Crea 20 productos
            crearPedidos(15, usuarios, productos); // Crea 15 pedidos aleatorios

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

    private List<Categoria> crearCategorias() {
        List<Categoria> categorias = new ArrayList<>();
        String[] nombres = {"Anillos", "Collares", "Pulseras", "Relojes", "Aros", "Piedras Preciosas"};
        
        for (String nombre : nombres) {
            Categoria c = new Categoria();
            c.setNombreCategoria(nombre);
            categorias.add(categoriaRepository.save(c));
        }
        return categorias;
    }

    private List<Producto> crearProductos(int cantidad, List<Categoria> categorias) {
        List<Producto> productos = new ArrayList<>();
        for (int i = 0; i < cantidad; i++) {
            Producto p = new Producto();
            Categoria cat = categorias.get(faker.random().nextInt(categorias.size()));
            
            String material = faker.options().option("Oro", "Plata", "Platino", "Acero");
            String tipo = cat.getNombreCategoria(); // Ej: "Anillo"
            
            p.setNombreProducto(tipo + " de " + material + " " + faker.ancient().god());
            p.setDescripcionProducto("Hermoso " + tipo.toLowerCase() + " fabricado en " + material + ". " + faker.lorem().sentence());
            p.setPrecio(faker.number().numberBetween(15000, 500000));
            p.setStock(faker.number().numberBetween(0, 50));
            p.setCategoria(cat);
            
            productos.add(productoRepository.save(p));
        }
        return productos;
    }

    private void crearPedidos(int cantidad, List<Usuario> usuarios, List<Producto> productos) {
        for (int i = 0; i < cantidad; i++) {
            Pedido pedido = new Pedido();
            Usuario cliente = usuarios.get(faker.random().nextInt(usuarios.size()));
            
            pedido.setUsuario(cliente);
            pedido.setFechaPedido(faker.date().past(30, TimeUnit.DAYS));
            pedido.setDireccionEnvio(faker.address().fullAddress());
            pedido.setMetodoPago(faker.options().option("WebPay", "Transferencia", "Crédito"));
            
            // Estado aleatorio
            String estado = faker.options().option("Pendiente", "Pagado", "Enviado", "Entregado");
            pedido.setEstadoPedido(estado);
            
            // Guardamos primero para tener ID
            pedido.setTotalPedido(0); // Se calcula después
            pedido = pedidoRepository.save(pedido);

            // Crear detalles (productos del pedido)
            int numDetalles = faker.number().numberBetween(1, 4);
            int total = 0;
            
            for (int j = 0; j < numDetalles; j++) {
                DetallePedido det = new DetallePedido();
                Producto prod = productos.get(faker.random().nextInt(productos.size()));
                int cantidadProd = faker.number().numberBetween(1, 3);
                
                det.setPedido(pedido);
                det.setProducto(prod);
                det.setCantidadProducto(cantidadProd);
                // Ojo: Manejar posibles nulos en precio si fuera necesario, aquí asumimos que tienen
                det.setSubtotal(prod.getPrecio() * cantidadProd);
                
                detallePedidoRepository.save(det);
                total += det.getSubtotal();
            }
            
            // Actualizar total
            pedido.setTotalPedido(total);
            pedidoRepository.save(pedido);

            // Si el pedido está Enviado o Entregado, crear Envío
            if (estado.equals("Enviado") || estado.equals("Entregado")) {
                Envio envio = new Envio();
                envio.setPedido(pedido);
                envio.setFecha_envio(faker.date().future(5, TimeUnit.DAYS, pedido.getFechaPedido()));
                envio.setEstado_envio(estado.equals("Enviado") ? "En Camino" : "Entregado");
                envioRepository.save(envio);
            }
        }
    }
}