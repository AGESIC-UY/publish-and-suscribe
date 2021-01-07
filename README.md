# Servicio de novedades de la Plataforma de Interoperabilidad

## Introducción
Para algunos sistemas, el uso de comunicaciones sincrónicas punto a punto no resulta viable debido a la rigidez y complejidad que puede tener su desarrollo dentro de ese sistema. Este tipo de sistemas demandan modelos de comunicación más flexibles, dinámicos y de bajo acoplamiento que permitan una mejor implementación de los procesos de negocios. En este sentido, la posibilidad de contar con una infraestructura de middleware dedicada que brinde las caracteristicas antes mencionadas, puede mejorar el desarrollo de este tipo de aplicaciones. 

Para dar soporte a esta solución, dado que la plataforma de interoperabilidad de AGESIC brinda capacidades de integración y middleware entre proveedores y consumidores de servicios, también ofrece el servicio de Publish & Subsribe (P&S), brindando una infraestructura de software que facilite el intercambio de novedades basado en suscripciones entre los organismos del Estado.

## Descripción de la solución
El sistema _Publish & Subscribe_ está basado en mecanismos de comunicación de tipo broadcast en donde un productor notifica cierta información a varios interesados. Algunos términos importante son:
- **Productor**: Es el sistema encargado de producir novedades en la Plataforma.
- **Suscriptor**: Es el sistema que desea recibir novedades acerca de un determinado tópico.
- **Tópico**: Se utilizará el concepto de tópico para identificar el canal al cual se subscriben los consumidores de novedades, un subscriptor puede estar asociado a múltiples tópicos y un productor puede generar mensajes también en múltiples tópicos. En la interfaz Web del sistema se podrán dar de alta los tópicos y administrar su asociación con productores y consumidores.

Dentro del contexto de _Publish & Subscribe_ los productores comunican la información a los suscriptores por intermedio de canales de comunicación denominados tópicos. Cada vez que un productor envía una novedad a un tópico, el sistema _Publish & Subscribe_ se encarga de retransmitirlo a cada uno de los suscriptores interesados.

Actualmente, existen dos mecanismos de entrega de novedades: **push** y **pull**. El mecanismo **push** consiste en que los suscriptores definen previamente dónde recibir las novedades. Cada vez que el servicio recibe una novedad, este la reenviará al destino definido por el suscriptor. Por otro lado, el mecanismo **pull** consiste en que el suscriptor consulta a la Plataforma en busca de novedades. Cada vez que el servicio recibe una novedad, este la almacena y queda a la
espera que el suscriptor lo consulte y la retire.

Es importante aclarar que el sistema almacena la novedad hasta que es enviada a todos los destinatarios y que las mismas son enviadas a cada uno de ellos en el mismo orden en que fueron generadas en el sistema, esto quiere decir que hasta que el destinatario no procese la novedad que se le está enviando, no se le enviará la siguiente.

_[Falta aclarar en qué consiste que el destinatario procese la novedad]_

Por último es importante aclarar también que en esta última versión se incorporó la posibilidad de aplicar filtros en los tópicos, de forma de poder "seleccionar" que novedad debe ser enviada o no a cada destinatario.

## Ejecución


## Contacto
Por cualquier duda o consulta, puede comunicarse a arquitectura@agesic.gub.uy

---

## Introduction


## Solution description


## Execution


## Contact
If you require any further information, please contact arquitectura@agesic.gub.uy


