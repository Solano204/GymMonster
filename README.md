<style>
  body {
    font-family: 'Arial', sans-serif;
    line-height: 1.6;
    background-color: #f4f4f4;
    padding: 20px;
  }

  h1 {
    font-size: 36px;
    color: #3498db;
    text-align: center;
    margin-bottom: 20px;
  }

  h2 {
    font-size: 28px;
    color: #e74c3c;
    margin-top: 30px;
  }

  h3 {
    font-size: 24px;
    color: #9b59b6;
    margin-top: 20px;
  }

  h4 {
    font-size: 22px;
    color: #2ecc71;
    margin-top: 15px;
  }

  p {
    font-size: 18px;
    color: #34495e;
    margin: 10px 0;
  }

  ul {
    list-style-type: none;
    padding: 0;
  }

  li {
    font-size: 18px;
    margin: 5px 0;
  }

  .section {
    border: 2px solid #e0e0e0;
    padding: 15px;
    border-radius: 10px;
    background-color: #fff;
    margin: 20px 0;
    box-shadow: 0 2px 5px rgba(0, 0, 0, 0.1);
  }

  .icon {
    font-size: 1.3em;
    margin-right: 5px;
    color: #3498db;
  }

  .centered-image {
    text-align: center;
  }

  .centered-image img {
    width: 80%;
    max-width: 600px;
    border-radius: 10px;
    box-shadow: 0 2px 5px rgba(0, 0, 0, 0.1);
  }
</style>

<h1>🌟 Descripción del Proyecto: Sistema de Gestión de Clientes para Gimnasios</h1>

<p>
  Nos complace presentar una innovadora <strong>aplicación de gestión de clientes para gimnasios</strong>, diseñada para optimizar el registro y la interacción de los usuarios. Este proyecto refleja nuestro compromiso con la calidad y la eficiencia, utilizando <strong>tecnologías de vanguardia</strong> para ofrecer una experiencia fluida tanto a administradores como a clientes.
</p>

<div class="section">
  <h2>🏗️ Arquitectura de Microservicios y Paradigma Asíncrono</h2>
  <p>
    La aplicación se basa en una <strong>arquitectura de microservicios</strong> junto a la <strong>arquitectura hexagonal</strong>, lo que permite una escalabilidad excepcional y una fácil mantenibilidad. Cada servicio se encarga de una función específica, garantizando flexibilidad y facilidad de actualización. Además, utilizamos un <strong>paradigma asíncrono (WebFlux)</strong>, que permite manejar múltiples solicitudes de manera simultánea y eficiente, mejorando significativamente la experiencia del usuario y los tiempos de respuesta.
  </p>
</div>

<style>
  .centered-image {
    max-width: 800px; /* maximum container width */
    margin: 0 auto;
    text-align: center; /* centers text within the div */
  }
  
  .centered-image img {
    width: 100%;
    height: auto;
  }
</style>

<div class="centered-image">
  <strong style="color: #2ecc71; font-size: 22px;">📊 Diagrama del Flujo de Comunicación entre Microservicios</strong>
  <img src="https://github.com/user-attachments/assets/3bd05d2a-378e-46c8-97ca-36c39a8fb7dc" alt="Diagrama del Flujo de Comunicación">
</div>


<div class="section">
  <h2>🌍 Implementaciones en Diferentes Entornos</h2>
  <p>El sistema cuenta con <strong>tres versiones</strong>: Local, Docker, y Kubernetes.</p>
</div>

<div class="section">
  <h2>🔑 Servicios Clave Incluidos en la Aplicación</h2>
  <ul>
    <li>🔍 <strong>Eureka</strong>: Proporciona servicios de descubrimiento, facilitando la localización y comunicación entre microservicios, mejorando así la disponibilidad y resiliencia del sistema.</li>
    <li>🌐 <strong>API Gateway</strong>: Actúa como el punto de entrada para todas las solicitudes del cliente.</li>
    <li>🔒 <strong>Keycloak</strong>: Gestiona la seguridad y el acceso de los usuarios, permitiendo el registro, autenticación y autorización segura mediante <strong>JWT Tokens</strong>.</li>
  </ul>
</div>

<div class="section">
  <h2>🎯 ¿Por Qué Elegir Este Proyecto?</h2>
  <p>Este sistema es una herramienta integral que transforma la forma en que los gimnasios gestionan sus operaciones.</p>
</div>

<!-- New Section: Login Flow -->
<div class="section">
  <h2>🔐 Proceso de Inicio de Sesión</h2>
  <p>
    El flujo de inicio de sesión utiliza <strong>Keycloak</strong> para gestionar la autenticación de usuarios. Los usuarios ingresan sus credenciales en la interfaz de inicio de sesión, la cual redirige a Keycloak para validarlas. Una vez autenticado, Keycloak genera un <strong>token JWT</strong> que es guardado en <Strong> Redis <Strong> que permite el acceso a los servicios autorizados.
  </p>
</div>

<div class="centered-image">
  <strong style="color: #2ecc71; font-size: 22px;">🔄 Diagrama del Flujo de Inicio de Sesión</strong>
  <img src="https://github.com/user-attachments/assets/a1d844c7-a93d-4aeb-a7f1-d01e57bd26b8" alt="Diagrama de Inicio de Sesión">
</div>

<!-- New Section: Logout Flow -->
<div class="section">
  <h2>🚪 Proceso de Cierre de Sesión</h2>
  <p>
    En el flujo de cierre de sesión, el usuario puede desconectarse desde la aplicación o desde el propio portal de Keycloak. Esto invalida el token JWT y revoca los permisos, asegurando que el usuario no pueda acceder a los servicios hasta que vuelva a iniciar sesión.
  </p>
</div>



<div class="centered-image">
  <strong style="color: #2ecc71; font-size: 22px;">🔄 Diagrama del Flujo de Cierre de Sesión</strong>
  <img src="https://github.com/user-attachments/assets/06bf340a-6001-4715-aae1-bc84c70d28f5" alt="Diagrama de Cierre de Sesión">
</div>


- **Technologies**: Java, Spring Boot, WebFlux, Docker, Kubernetes, Redis, Kafka, Keycloak, Mysql with R2DBC,Spring cloud 