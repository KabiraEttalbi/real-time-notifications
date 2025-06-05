# Plateforme de Notifications en Temps Réel

## Architecture

Cette plateforme utilise une architecture événementielle basée sur Apache Kafka avec les microservices suivants :

### Services

1. **User Service** (Port 8081)
   - Gestion des utilisateurs (inscription, authentification)
   - Émet l'événement `UserRegistered`

2. **Notification Service** (Port 8082)
   - Traitement des notifications (Email, SMS, Push)
   - Consomme les événements `UserRegistered` et `NotificationTriggered`

3. **Event Publisher Service** (Port 8083)
   - API pour déclencher des événements manuellement
   - Émet les événements `NotificationTriggered` et `UserActionPerformed`

4. **Logging Service** (Port 8084)
   - Audit et logging de tous les événements
   - API pour consulter les logs

5. **Web Interface** (Port 8085)
   - Interface web complète pour la gestion de la plateforme
   - Dashboard en temps réel avec WebSocket
   - Formulaires d'inscription et d'envoi de notifications
   - Monitoring et visualisation des événements

### Infrastructure

- **Apache Kafka** : Broker de messages pour l'architecture événementielle
- **MySQL** : Base de données pour la persistance
- **Kafka UI** : Interface de monitoring Kafka
- **Kubernetes** : Orchestration des services

## Déploiement sur Kubernetes

### Prérequis
- Kubernetes cluster (Minikube, GKE, EKS, AKS, etc.)
- kubectl configuré
- Docker registry accessible
- Java 17
- Maven

### Étapes de déploiement

1. Construire et pousser les images Docker :
\`\`\`bash
# Modifier le registre Docker dans le script
chmod +x scripts/build-docker-images.sh
./scripts/build-docker-images.sh
\`\`\`

2. Déployer sur Kubernetes :
\`\`\`bash
chmod +x scripts/deploy-to-kubernetes.sh
./scripts/deploy-to-kubernetes.sh
\`\`\`

3. Vérifier le déploiement :
\`\`\`bash
kubectl get pods -n notifications-platform
kubectl get services -n notifications-platform
kubectl get ingress -n notifications-platform
\`\`\`

### Accès aux services

- Interface Web: http://notifications.example.com
- Dashboard: http://notifications.example.com/dashboard
- Notifications: http://notifications.example.com/notifications
- Monitoring: http://notifications.example.com/monitoring
- API Gateway: http://notifications.example.com/api
  - User Service: /api/users
  - Notification Service: /api/notifications
  - Event Publisher: /api/events
  - Logging Service: /api/logs
- Kafka UI: http://notifications.example.com/kafka-ui

## Événements

### UserRegistered
Émis lors de l'inscription d'un utilisateur
\`\`\`json
{
  "userId": "uuid",
  "email": "user@example.com",
  "firstName": "John",
  "lastName": "Doe",
  "timestamp": "2024-01-01T10:00:00"
}
\`\`\`

### NotificationTriggered
Émis pour déclencher une notification
\`\`\`json
{
  "notificationId": "uuid",
  "userId": "uuid",
  "type": "EMAIL|SMS|PUSH",
  "title": "Titre",
  "message": "Message",
  "recipient": "destinataire",
  "timestamp": "2024-01-01T10:00:00"
}
\`\`\`

### UserActionPerformed
Émis lors d'une action utilisateur
\`\`\`json
{
  "userId": "uuid",
  "actionType": "LOGIN|LOGOUT|UPDATE_PROFILE",
  "actionDetails": "Détails de l'action",
  "timestamp": "2024-01-01T10:00:00"
}
\`\`\`

## Tests

### Tests unitaires
\`\`\`bash
mvn test
\`\`\`

### Tests d'intégration avec TestContainers
\`\`\`bash
mvn verify
\`\`\`

## Monitoring

- Kafka UI : http://notifications.example.com/kafka-ui
- Logs des services : `kubectl logs -n notifications-platform deployment/[service-name]`
- API de logs : http://notifications.example.com/api/logs

## Résilience

L'architecture implémente :
- Retry automatique des messages Kafka
- Dead Letter Queue pour les messages échoués
- Readiness et Liveness probes pour Kubernetes
- Monitoring et alerting
