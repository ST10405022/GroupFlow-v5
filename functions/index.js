// functions/index.js
const { onValueCreated } = require("firebase-functions/v2/database");
const admin = require("firebase-admin");
const logger = require("firebase-functions/logger");

admin.initializeApp();
const db = admin.database();

/**
 * Send FCM notification to a device
 */
async function sendFcmNotification(token, payload) {
  try {
    const response = await admin.messaging().send({
      token,
      data: payload.data,
      android: { priority: "high" },
      notification: {
        title: payload.title,
        body: payload.body,
      },
    });
    logger.log("✅ FCM sent:", response);
  } catch (err) {
    logger.error("❌ Error sending FCM:", err);

    if (
      err.code === "messaging/registration-token-not-registered" ||
      err.code === "messaging/invalid-registration-token"
    ) {
      // Remove invalid token
      const snapshot = await db
        .ref("/users")
        .orderByChild("fcmToken")
        .equalTo(token)
        .get();

      snapshot.forEach((userSnap) => {
        userSnap.ref.child("fcmToken").remove();
        logger.log("🗑️ Removed invalid FCM token for:", userSnap.key);
      });
    }
  }
}

/**
 * Utility: Get all users by role
 */
async function getUsersByRole(role) {
  const snap = await db.ref("/users").orderByChild("role").equalTo(role).get();
  const users = [];
  snap.forEach((userSnap) => {
    const fcmToken = userSnap.child("fcmToken").val();
    if (fcmToken) users.push({ id: userSnap.key, fcmToken });
  });
  return users;
}

/**
 * Utility: Create a notification in DB
 */
async function createNotification(recipientId, message, type, relatedId) {
  const notifId = db.ref("/notifications").push().key;
  await db.ref(`/notifications/${notifId}`).set({
    id: notifId,
    recipientId,
    message,
    timestamp: Date.now(),
    read: false,
    type,
    relatedId,
  });
}

/**
 * Trigger: New Ultrascan
 */
exports.notifyOnNewUltrascan = onValueCreated(
  { ref: "/ultrascans/{scanId}", instance: "groupflow-520ee-default-rtdb" },
  async (event) => {
    const scanId = event.params.scanId;
    const scanData = event.data && event.data.val();

    if (!scanData) {
      logger.warn("⚠️ No scan data for ID:", scanId);
      return null;
    }

    logger.log("🩻 New Ultrascan:", scanId, scanData);

    const fileName = scanData.fileName || "Unknown file";
    const patientId = scanData.patientId;

    if (!patientId) {
      logger.warn("⚠️ No patientId on scan:", scanId);
      return null;
    }

    // Patient
    const patientSnap = await db.ref(`/users/${patientId}`).get();
    const patientToken = patientSnap.child("fcmToken").val();
    const patientName = patientSnap.child("name").val() || "Your";

    // Employees
    const employees = await getUsersByRole("EMPLOYEE");

    // Payloads
    const patientPayload = {
      title: "Your Ultrascan is Ready",
      body: `A new ultrascan has been uploaded: ${fileName}`,
      data: {
        scanId,
        patientId,
        fileName,
        clickActionTarget: "ULTRASCAN_SCREEN",
      },
    };

    const employeePayload = {
      title: "Patient Ultrascan Uploaded",
      body: `${patientName} has a new ultrascan: ${fileName}`,
      data: {
        scanId,
        patientId,
        uploaderId: scanData.uploaderId,
        fileName,
        clickActionTarget: "ULTRASCAN_SCREEN",
      },
    };

    // Send + Persist
    const tasks = [];
    if (patientToken) {
      tasks.push(sendFcmNotification(patientToken, patientPayload));
      tasks.push(createNotification(
        patientId,
        patientPayload.body,
        "ULTRASCAN",
        scanId
      ));
    }

    for (const e of employees) {
      tasks.push(sendFcmNotification(e.fcmToken, employeePayload));
      tasks.push(createNotification(
        e.id,
        employeePayload.body,
        "ULTRASCAN",
        scanId
      ));
    }

    await Promise.all(tasks);
    return null;
  }
);

/**
 * Trigger: New Appointment
 */
exports.notifyOnNewAppointment = onValueCreated(
  { ref: "/appointments/{appointmentId}", instance: "groupflow-520ee-default-rtdb" },
  async (event) => {
    const appointmentId = event.params.appointmentId;
    const appointmentData = event.data && event.data.val();

    if (!appointmentData) {
      logger.warn("⚠️ No appointment data for:", appointmentId);
      return null;
    }

    logger.log("📅 New Appointment:", appointmentId, appointmentData);

    const patientId = appointmentData.patientId;
    if (!patientId) {
      logger.warn("⚠️ Appointment missing patientId:", appointmentId);
      return null;
    }

    // Patient
    const patientSnap = await db.ref(`/users/${patientId}`).get();
    const patientToken = patientSnap.child("fcmToken").val();
    const patientName = patientSnap.child("name").val() || "Patient";

    // Employees
    const employees = await getUsersByRole("EMPLOYEE");

    // Payloads
    const patientPayload = {
      title: "New Appointment Scheduled",
      body: "You have a new appointment scheduled.",
      data: {
        appointmentId,
        clickActionTarget: "APPOINTMENTS_SCREEN",
      },
    };

    const employeePayload = {
      title: "New Appointment Created",
      body: `${patientName} has a new appointment scheduled.`,
      data: {
        appointmentId,
        patientId,
        clickActionTarget: "APPOINTMENTS_SCREEN",
      },
    };

    // Send + Persist
    const tasks = [];
    if (patientToken) {
      tasks.push(sendFcmNotification(patientToken, patientPayload));
      tasks.push(createNotification(
        patientId,
        patientPayload.body,
        "APPOINTMENT",
        appointmentId
      ));
    }

    for (const e of employees) {
      tasks.push(sendFcmNotification(e.fcmToken, employeePayload));
      tasks.push(createNotification(
        e.id,
        employeePayload.body,
        "APPOINTMENT",
        appointmentId
      ));
    }

    await Promise.all(tasks);
    return null;
  }
);
