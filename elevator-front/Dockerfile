FROM node:16

WORKDIR /usr/src/app

COPY --chown=node:node . .
RUN npm ci

ENV DEBUG=playground:*
RUN chown -R node /usr/src/app/node_modules
USER node
CMD npm start