# Arquitectura del backend: DDD en 4 capas + CQRS

Cada módulo (bounded context) del backend sigue la misma estructura:

```
com.smartbusiness.<modulo>/
├── interfaces/rest/                  ← INTERFACES
│   ├── XController                     endpoints HTTP (mismas rutas de siempre)
│   ├── resources/                      DTOs de entrada/salida (Request / Response)
│   └── transform/                      assemblers: Request → Command/Query, Entidad → Response
├── application/internal/             ← APPLICATION
│   ├── commandservices/                implementación de los Commands
│   └── queryservices/                  implementación de las Queries
├── domain/                           ← DOMAIN
│   ├── model/aggregates|entities       entidades del negocio
│   ├── model/valueobjects              enums y objetos de valor
│   ├── model/commands                  Commands (records)
│   ├── model/queries                   Queries (records)
│   ├── model/readmodels                resultados de lectura compuestos
│   └── services/                       interfaces XCommandService / XQueryService
└── infrastructure/persistence/jpa/   ← INFRASTRUCTURE
    └── repositories/                   repositorios Spring Data JPA
```

Módulos: `catalogo`, `inventario`, `ventas`, `negocio`, `usuario`, `ia`, más `shared`
(excepciones de dominio, seguridad JWT, contexto multi-negocio y manejo global de errores).

## Dependencias entre capas (como en el diagrama)

- **Interfaces → Application**: los controllers solo conocen las interfaces
  `XCommandService` / `XQueryService` del dominio y les envían Commands o Queries.
- **Application → Domain**: las implementaciones usan las entidades, Commands, Queries
  y las interfaces de servicio definidas en `domain`.
- **Infrastructure → Domain**: los repositorios JPA trabajan sobre las entidades del dominio.
- **Inyección de dependencias**: Spring inyecta por constructor las implementaciones
  (`...ServiceImpl`) y los repositorios de infraestructura donde se piden sus interfaces.

## CQRS (Command and Query Responsibility Segregation)

| Tipo | Qué hace | Ejemplos |
|---|---|---|
| **Command** – modifica el estado de la BD | crea, actualiza o borra datos (`@Transactional`) | `CrearProductoCommand`, `RegistrarVentaCommand`, `RegistrarMovimientoCommand`, `CrearNegocioCommand`, `CrearEmpleadoCommand`, `CambiarPasswordCommand` |
| **Query** – consulta a la base de datos | solo lee (`@Transactional(readOnly = true)`) | `ObtenerProductosActivosQuery`, `ObtenerVentasQuery`, `ObtenerMisNegociosQuery`, `ObtenerEmpleadosQuery`, `IniciarSesionQuery` |
| **Query** – consulta a lógica algorítmica | calcula resultados sin modificar datos | `ObtenerAlertasStockQuery` (riesgo de quiebre), `ObtenerAlertasDemandaQuery` (variación de demanda), `ConsultarAsistenteQuery` (asistente IA) |

Cada módulo tiene un servicio para Commands y otro para Queries, por ejemplo
`ProductoCommandService` y `ProductoQueryService`. Así, la escritura y la lectura
nunca quedan mezcladas en la misma clase.

## Contrato con el frontend

La refactorización no cambió la API: las rutas, los métodos HTTP, los nombres de los
campos JSON, los códigos de estado y los mensajes de error son los mismos que antes.
